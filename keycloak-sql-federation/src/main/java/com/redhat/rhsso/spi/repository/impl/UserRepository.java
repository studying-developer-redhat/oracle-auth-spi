package com.redhat.rhsso.spi.repository.impl;

import com.redhat.rhsso.spi.model.builder.create.PersonCreateBuilder;
import com.redhat.rhsso.spi.model.builder.create.UserCreateBuilder;
import com.redhat.rhsso.spi.model.entity.Person;
import com.redhat.rhsso.spi.model.entity.User;
import org.jboss.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Stateless
public class UserRepository  {

    private static final Logger LOGGER = Logger.getLogger(UserRepository.class);

    @PersistenceContext(unitName = "keycloak-user-storage-jpa")
    protected EntityManager em;

    @EJB
    private PersonCreateBuilder personCreateBuilder;

    @EJB
    private UserCreateBuilder userCreateBuilder;

    public boolean validateCredentials(final String username, final String password) {
        LOGGER.infof("validateCredentials() {} {}", username, password);
        final String encryptedPassword = encrypt(password);
        final User user = findUserByUsername(username);
        if (user == null) {
            LOGGER.warn("User with username " + username + " not found");
            return false;
        }
        LOGGER.info("User Password: " + password + ", Encrypted Password: " + encryptedPassword);
        return user.getPassword() != null && user.getPassword().equals(encryptedPassword);
    }

    private String encrypt(final String plainPassword) {
        // TODO: You need to implement the algorithm
        return plainPassword;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public User updateCredentials(final String username, final String password) {
        LOGGER.infof("updateCredentials() {} {}", username, password);
        final String encryptedPassword = encrypt(password);
        final User user = findUserByUsername(username);
        if (user == null) {
            LOGGER.warn("User with username " + username + " not found");
            return null;
        }
        user.setPassword(encryptedPassword);
        em.persist(user);
        return user;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public User save(final String username) {
        LOGGER.infof("save() {} ", username);
        final Person person = persistPerson(username);
        final User user = persistUser(person);
        LOGGER.infof("Saving user: {}", user);
        return user;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean remove(final String externalId) {
        LOGGER.infof("remove() {} ", externalId);
        final User user = this.findUserByUsername(externalId);
        if (user == null) {
            return false;
        }
        em.remove(user);
        return true;
    }

    public int getUsersCount() {
        LOGGER.info("getUsersCount() called");
        return ((Number) em.createNamedQuery(User.GET_USER_COUNT).getSingleResult()).intValue();
    }

    /*
    public int getUsersCount() {
        final TypedQuery<Long> query = em.createNamedQuery("count", Long.class);
        return query.getSingleResult() != null ? query.getSingleResult().intValue() : 0;
    }
     */

    public Collection<User> findAll() {
        LOGGER.info("findAll() called");
        return em.createNamedQuery(User.FIND_ALL, User.class).getResultList();
    }

    public Collection<User> findAll(final int firstResult, final int maxResults) {
        LOGGER.info("findAll() firstResult, maxResults called");
        return em.createNamedQuery(User.FIND_ALL, User.class)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public Collection<User> findAllByUsernameOrEmail(final String search) {
        LOGGER.info("findAllByUsernameOrEmail() " + search);
        return em.createNamedQuery(User.FIND_BY_USERNAME_OR_EMAIL, User.class)
                .setParameter("search", search != null ? search.trim().toLowerCase() : "")
                .getResultList();
    }

    public Collection<User> findAllByUsernameOrEmail(final String search, final int firstResult,
                                                     final int maxResults) {
        LOGGER.info("findAllByUsernameOrEmail() firstResult, maxResults" + search);
        return em.createNamedQuery(User.FIND_BY_USERNAME_OR_EMAIL, User.class)
                .setParameter("search", search != null ? search.trim().toLowerCase() : "")
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }


    public User findUserByUsername(final String username) {
        try {
            LOGGER.info("findUserByUsername() " + username);
            User user = em.createNamedQuery(User.FIND_BY_USERNAME, User.class)
                    .setParameter("username", username != null ? username.trim().toLowerCase() : "")
                    .getSingleResult();
            LOGGER.info("findUserByUsername() User" + user.toString());
            return user;
        } catch (final NoResultException e) {
            LOGGER.warn("No result found for username " + username);
            return null;
        } catch (final NonUniqueResultException e) {
            LOGGER.warn("More than one result for username " + username);
            return null;
        }
    }

    public User findUserByEmail(final String email) {
        try {
            LOGGER.info("findUserByEmail() " + email);
            return em.createNamedQuery(User.FIND_BY_EMAIL, User.class)
                    .setParameter("email", email != null ? email.trim().toLowerCase() : "")
                    .getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("No result found for email " + email);
            return null;
        } catch (NonUniqueResultException e) {
            LOGGER.warn("More than one result for email " + email);
            return null;
        }
    }


    private User persistUser(Person person) {
        final User user = userCreateBuilder.build(person);
        em.persist(user);
        em.flush();
        return user;
    }

    private Person persistPerson(String username) {
        final Person person = personCreateBuilder.build(username);
        em.persist(person);
        em.flush();
        return person;
    }
}

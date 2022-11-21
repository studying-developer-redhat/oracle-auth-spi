package com.redhat.rhsso.spi.repository.impl;

import com.redhat.rhsso.spi.adapter.UserAdapter;
import com.redhat.rhsso.spi.config.UserFederationConfig;
import com.redhat.rhsso.spi.helper.IdentityHelper;
import com.redhat.rhsso.spi.model.builder.create.PersonCreateBuilder;
import com.redhat.rhsso.spi.model.builder.create.UserCreateBuilder;
import com.redhat.rhsso.spi.model.entity.Person;
import com.redhat.rhsso.spi.model.entity.User;
import com.redhat.rhsso.spi.repository.BaseFederationRepository;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Transactional
public class UserRepository extends BaseFederationRepository<User> {

    protected static final Logger logger = Logger.getLogger(UserRepository.class);

    protected UserFederationConfig config;

    protected ComponentModel model;

    protected KeycloakSession session;

    @PersistenceContext
    protected EntityManager em;

    @EJB
    private PersonCreateBuilder personCreateBuilder;

    @EJB
    private UserCreateBuilder userCreateBuilder;

    public UserFederationConfig getConfig() {
        return config;
    }

    public void setConfig(UserFederationConfig config) {
        this.config = config;
    }

    @PostConstruct
    protected void init() {
        logger.info("initializing " + UserRepository.class.getSimpleName());
    }

    @Override
    public User getUserById(Long id) {
        logger.info("getUserById() called with id: " + id);

        User entity = em.find(User.class, id);

        if (entity == null || entity.getId() == null) {
            logger.info("could not find user by id:" + id);
            return null;
        }

        logger.info("Found user id: " + entity.getId());

        return entity;
    }

    public User getUserByUsername(String username) {
        logger.info("getUserByUsername() called with username: " + username);
        List<User> result = Collections.EMPTY_LIST;

        try {
            TypedQuery<User> query = em.createNamedQuery(User.GET_USER_BY_USERNAME, User.class);
            query.setParameter("username", username);

            result = query.getResultList();

            if (result.isEmpty()) {
                logger.infof("username {} not found", username);
            }
        }catch (Exception e) {
            logger.infof("getUserByUsername() failed with message: {}", e.getMessage());
        }

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public User getUserByEmail(final String email) {
        logger.infof("getUserByEmail() called with email: {}", email);

        TypedQuery<User> query = em.createNamedQuery(User.GET_USER_BY_EMAIL,  User.class);
        query.setParameter("email", email);
        List<User> result = query.getResultList();
        if (result.isEmpty()) {
            logger.infof("could not find user by email: {}", email);
        }
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public int getUsersCount() {
        logger.info("getUsersCount() called");
        return ((Number) em.createNamedQuery(User.GET_USER_COUNT).getSingleResult()).intValue();
    }


    @Override
    public List<UserModel> getUsers(Integer firstResult, Integer maxResults, RealmModel realm) {
        logger.info("getUsers called");
        TypedQuery<User> query = em.createNamedQuery(User.GET_ALL_USERS, User.class);
        List<User> results = query.getResultList();

        if (firstResult >= 0)
            query.setFirstResult(firstResult);
        if (maxResults >= 1)
            query.setMaxResults(maxResults);

        if (results.isEmpty()) {
            logger.debug("No users found");
            return Collections.EMPTY_LIST;
        }

        return getUsersModel(realm, results);
    }

    @Override
    public List<UserModel> searchForUserByUsernameOrEmail(String search, Integer firstResult, Integer maxResults,
                                                          RealmModel realm) {
        logger.infof("searchForUserByUsernameOrEmail called with search: {}", search);

        TypedQuery<User> query = em.createNamedQuery(User.SEARCH_FOR_USERNAME_OR_EMAIL, User.class);
        query.setParameter("search", "%" + search.toLowerCase() + "%");

        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }

        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }

        List<User> results = query.getResultList();

        if (results.isEmpty()) {
            logger.debug("User not found");
            return Collections.EMPTY_LIST;
        }

        return getUsersModel(realm, results);
    }

    // https://access.redhat.com/solutions/32314
    // https://stackoverflow.com/questions/2506411/how-to-troubleshoot-ora-02049-and-lock-problems-in-general-with-oracle
    @Override
    public UserModel addUser(final String username, final RealmModel realm) {
        try {
            logger.infof("addUser() called with username: {}", username);
            isValidUsername(username);
            final Person person = persistPerson(username);
            final User user = persistUser(person);
            final UserAdapter userAdapter = new UserAdapter(this.session, realm, this.model, user);
            logger.info("addUser() successful User: " + user);
            logger.info("addUser() userAdapter: " + userAdapter);
            return userAdapter;
        } catch (PersistenceException e) {
            logger.errorf("[ERROR] addUser() {} : {}", username, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean updateUser(final User entity) {
        logger.info("updateUser() called: " + entity);
        if (entity == null) return false;
        em.merge(entity);
        logger.info("updateUser() successful updated user: " + entity);
        return true;
    }


    @Override
    public Boolean removeUser(final String externalId) {
        logger.infof("removeUser() called with externalId: {}", externalId);
        User entity = getUserByUsername(externalId);
        if (entity == null) return false;
        em.remove(entity.getPerson());
        em.flush();
        logger.infof("successful() removed user: {}", entity.getUsername());
        return true;
    }

    @Override
    public Boolean isValidPassword(String credential) {
        return IdentityHelper.isValidPassword(credential, this.config);
    }

    @Remove
    public void close() {
        logger.info(UserRepository.class.getSimpleName() + " closing...");
    }

    private List<UserModel> getUsersModel(RealmModel realm, List<User> results) {
        return results.stream().map(entity -> new UserAdapter(this.session, realm, this.model, entity))
                .collect(Collectors.toCollection(LinkedList::new));
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

    private void isValidUsername(String username) {
        if (!IdentityHelper.isValidUsername(em, username)) {
            logger.errorf("Username {} already exists", username);
            throw new RuntimeException("Username already exists.");
        }
    }
}

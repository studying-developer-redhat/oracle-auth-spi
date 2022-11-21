package com.redhat.rhsso.spi.repository.impl;

import com.redhat.rhsso.spi.adapter.UserAdapter;
import com.redhat.rhsso.spi.config.UserFederationConfig;
import com.redhat.rhsso.spi.helper.CredentialHelper;
import com.redhat.rhsso.spi.helper.IdentityHelper;
import com.redhat.rhsso.spi.model.entity.Person;
import com.redhat.rhsso.spi.model.entity.User;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.redhat.rhsso.spi.repository.BaseFederationRepository;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class UserRepository extends BaseFederationRepository<User> {

    protected static final Logger logger = Logger.getLogger(UserRepository.class);

    protected UserFederationConfig config;

    protected ComponentModel model;

    protected KeycloakSession session;

    @PersistenceContext
    EntityManager em;

    @Resource
    private UserTransaction userTransaction;

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
        logger.debugf("getUserById called with id = {}", id);

        User entity = em.find(User.class, id);

        if (entity == null || entity.getId() == null) {
            logger.debugf("could not find user by id: {}", id);
            return null;
        }

        logger.debugf("Found user id: {}", entity.getId());

        return entity;
    }

    @Override
    public User getUserByUsername(String username) {
        logger.debugf("getUserByUsername called with username = {}", username);

        List<User> result = Collections.EMPTY_LIST;

        try {
            TypedQuery<User> query = em.createNamedQuery("getUserByUsername", User.class);
            query.setParameter("username", username);
            query.setHint("org.hibernate.comment", "search user by username.");

            result = query.getResultList();

            if (result.isEmpty()) {
                logger.debugf("username {} not found", username);
            }
        }catch (Exception e) {
            logger.debugf("getUserByUsername failed with message: {}", e.getMessage());
        }

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public User getUserByEmail(String email) {
        logger.debugf("getUserByEmail called with email = {}", email);

        if (this.config == null) {
            System.out.println("A");
        } else {
            if (this.config.queryUserByEmail() == null) {
                System.out.println("B");
            }
        }
        if (entityManager == null) {
            System.out.println("C");
        }

        TypedQuery<User> query = em.createQuery(this.config.queryUserByEmail(), User.class);
        query.setParameter("email", email);

        List<User> result = query.getResultList();

        if (result.isEmpty()) {
            logger.infof("could not find user by email: {}", email);
        }

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public int getUsersCount() {
        logger.debug("getUsersCount called");

        TypedQuery<User> query = em.createQuery(this.config.queryUserCount(), User.class);
        Object count = query.getSingleResult();
        return count == null ? null : ((Number) count).intValue();
    }

    @Override
    public List<UserModel> getUsers(Integer firstResult, Integer maxResults, RealmModel realm) {
        logger.debug("getUsers called");

        TypedQuery<User> query = em.createQuery(this.config.queryAllUsers(), User.class);

        List<User> results = query.getResultList();

        if (firstResult >= 0)
            query.setFirstResult(firstResult);
        if (maxResults >= 1)
            query.setMaxResults(maxResults);

        if (results.isEmpty()) {
            logger.debug("No users found");
            return Collections.EMPTY_LIST;
        }

        List<UserModel> users = new LinkedList<>();
        for (User entity : results) users.add(new UserAdapter(this.session, realm, this.model, entity));

        return users;
    }

    @Override
    public List<UserModel> searchForUserByUsernameOrEmail(String search, Integer firstResult, Integer maxResults, RealmModel realm) {
        logger.debugf("searchForUserByUsernameOrEmail called with search = {}", search);

        TypedQuery<User> query = em.createQuery(this.config.querySearchForUser(), User.class);

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

        List<UserModel> users = new LinkedList<>();

        for (User entity : results) users.add(new UserAdapter(this.session, realm, this.model, entity));
        return users;
    }

    // https://access.redhat.com/solutions/32314
    // https://stackoverflow.com/questions/2506411/how-to-troubleshoot-ora-02049-and-lock-problems-in-general-with-oracle
    @Override
    public UserModel addUser(String username, RealmModel realm) {
        logger.debugf("addUser called with username = {}", username);

        if (!IdentityHelper.isValidUsername(entityManager,this.config, username)) {
            logger.errorf("Username {} already exists", username);
            throw new RuntimeException("Username already exists.");
        }

        Person p = null;
        User u = null;

        try {
            userTransaction.begin();

            p = new Person();
            p.setName(username);
            p.setMiddle("-");
            p.setFamily("-");
            p.setIssn("-");
            p.setStatus("1");
            p.setCreation(Date.valueOf(LocalDate.now()));
            em.persist(p);
            em.flush();
            userTransaction.commit();
            em.getTransaction().commit();

            // User will not have direct access upon registration.
            // An update-password required action will be set for new users.
            u = new User();
            CredentialHelper temporaryCredential = new CredentialHelper.PasswordGeneratorBuilder()
                    .useDigits(true)
                    .useLower(true)
                    .useUpper(true)
                    .usePunctuation(true)
                    .build();

            u.setId(p.getId());
            u.setPerson(p);
            u.setUsername(username);
            u.setPassword(temporaryCredential.generate(20));
            u.setEmail("-");

            em.persist(u);
            em.flush();
            em.clear();
            userTransaction.commit();

        } catch (NotSupportedException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            e.printStackTrace();
        } catch (HeuristicRollbackException e) {
            e.printStackTrace();
        } catch (HeuristicMixedException e) {
            e.printStackTrace();
        } catch (RollbackException e) {
            e.printStackTrace();
        }
        finally {
            em.close();
            try {
                if (userTransaction.getStatus() == Status.STATUS_ACTIVE)
                    userTransaction.rollback();
            } catch (Throwable e) { }
        }

        UserAdapter userAdapter = new UserAdapter(this.session, realm, this.model, u);
        // userAdapter.setEmailVerified(false);
        // userAdapter.setEnabled(false);
        // userAdapter.setPassword(u.getPassword());

        logger.debugf("successful added user: {}", username);
        return userAdapter;
    }

    @Override
    public Boolean removeUser(String externalId) {
        logger.debugf("removeUser called with externalId = {}", externalId);

        User entity = getUserByUsername(externalId);
        if (entity == null) return false;

        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();

        logger.infof("successful removed user: {}", entity.getUsername());
        return true;
    }

    @Override
    public Boolean updateUser(User entity) {
        logger.debug("updateUser called");

        if (entity == null) return false;

        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();

        logger.infof("successful updated user: {}", entity.getUsername());
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

}

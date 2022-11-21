package com.redhat.rhsso.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.rhsso.spi.adapter.UserAdapter;
import com.redhat.rhsso.spi.config.UserFederationConfig;
import com.redhat.rhsso.spi.helper.CredentialHelper;
import com.redhat.rhsso.spi.model.User;
import com.redhat.rhsso.spi.repository.impl.UserRepository;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.keycloak.storage.UserStorageProviderModel;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.OnUserCache;
import java.util.*;

// https://access.redhat.com/documentation/en-us/red_hat_single_sign-on/7.6/html-single/server_developer_guide/index#user-storage-spi
@Stateful
@Local(OracleUserStorageProvider.class)
public class OracleUserStorageProvider
        implements UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator,
        CredentialInputUpdater,
        UserQueryProvider,
        UserRegistrationProvider {

    protected static final Logger logger = Logger.getLogger(OracleUserStorageProvider.class);

    public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";

    @PersistenceContext(name = "keycloak-user-storage-jpa")
    protected EntityManager em;

    protected ComponentModel model;

    protected KeycloakSession session;

    protected UserFederationConfig config;

    private ObjectMapper mapper;

    @EJB
    private UserRepository repository;

    @PostConstruct
    protected void init() {
        logger.info("initializing OracleUserStorageProvider");
        this.getRepository().setEntityManager(em);
        this.mapper = new ObjectMapper();
    }

    public void setModel(ComponentModel model) {
        this.model = model;
    }

    public void setSession(KeycloakSession session) {
        this.session = session;
    }

    public void setConfig(UserFederationConfig config) { this.config = config; }

    public UserFederationConfig getConfig() { return this.config; }

    public UserAdapter getUserAdapter(UserModel userModel) {
        UserAdapter adapter = null;
        if (userModel instanceof CachedUserModel) {
            adapter = (UserAdapter)((CachedUserModel) userModel).getDelegateForUpdate();
        } else {
            adapter = (UserAdapter) userModel;
        }
        return adapter;
    }

    @Override
    public boolean updateCredential(RealmModel realmModel, UserModel userModel, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;

        UserCredentialModel cred = (UserCredentialModel)input;

        if (!getRepository().isValidPassword(cred.getValue())) {
            return false;
        }

        User entity = new User().setUsername(userModel.getUsername());
        entity.setPassword(input.getChallengeResponse());
        getRepository().updateUser(entity);

        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realmModel, UserModel userModel, String credentialType) {
        if (!supportsCredentialType(credentialType)) return;
        getUserAdapter(userModel).setPassword(null);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realmModel, UserModel userModel) {
        if (getUserAdapter(userModel).getPassword() != null) {
            Set<String> set = new HashSet<>();
            set.add(CredentialModel.PASSWORD);
            return set;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return CredentialModel.PASSWORD.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return supportsCredentialType(credentialType) && getPassword(userModel) != null;
    }

    public String getPassword(UserModel userModel) {
        String password = null;

        if (userModel instanceof CachedUserModel) {
            password = (String)((CachedUserModel)userModel).getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (userModel instanceof UserAdapter) {
            password = ((UserAdapter)userModel).getPassword();
        }
        return password;
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        String password = getPassword(userModel);

        UserAdapter adapter = getUserAdapter(userModel);
        String enforced = CredentialHelper.enforce(cred.getValue(), adapter.getUsername());

        return password != null && password.equals(enforced);
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        logger.infof("getUserById: {}", StorageId.externalId(id));

        final String externalId = StorageId.externalId(id);
        User user = getRepository().getUserById(Long.valueOf(externalId));

        UserModel userModel = new UserAdapter(session, realm, model, user);

        try {
            userModel.setSingleAttribute("exampleAttribute", mapper.writeValueAsString("true"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return user == null ? null : userModel;
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        logger.infof("getUserByUsername: {}", username);

        User user = getRepository().getUserByUsername(username);

        return user == null ? null : new UserAdapter(session, realm, model, user);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        if (em == null) {
            System.out.println("A1");
        }

        User user = getRepository().getUserByEmail(email);

        return user == null ? null : new UserAdapter(session, realm, model, user);
    }

    @Override
    public int getUsersCount(RealmModel realmModel) {
        return getRepository().getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return getUsers(realm, 0, -1);
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return getRepository().getUsers(firstResult, maxResults, realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return searchForUser(search, realm, -1, -1);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return getRepository().searchForUserByUsernameOrEmail(search, firstResult, maxResults, realm);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> map, RealmModel realmModel, int i, int i1) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel, int i, int i1) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realmModel, GroupModel groupModel) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String s, String s1, RealmModel realmModel) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        return getRepository().addUser(username, realm);
    }

    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        final String externalId = StorageId.externalId(userModel.getId());
        return getRepository().removeUser(externalId);
    }

    public UserRepository getRepository() {
        if (getConfig() == null) {
            System.out.println("config is null in OracleUserStorageProvider");
        }else {
            System.out.println("config set properly in OracleUserStorageProvider");
        }

        this.repository.setConfig(getConfig());
        return this.repository;
    }

    @Remove
    @Override
    public void close() {
        this.repository.close();
        this.repository = null;
        logger.info("closing....");
    }

}

package com.redhat.rhsso.spi;

import com.redhat.rhsso.spi.adapter.UserAdapter;
import com.redhat.rhsso.spi.base.AbstractUserStorageProvider;
import com.redhat.rhsso.spi.model.entity.User;
import com.redhat.rhsso.spi.repository.impl.UserRepository;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.*;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;

import java.util.*;
import java.util.stream.Collectors;

// https://access.redhat.com/documentation/en-us/red_hat_single_sign-on/7.6/html-single/server_developer_guide/index#user-storage-spi

public class OracleUserStorageProvider extends AbstractUserStorageProvider {

    public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";
    private static final String USERS_QUERY_SEARCH = "keycloak.session.realm.users.query.search";
    private static final String ADMIN = "admin";
    private static final Logger LOGGER = Logger.getLogger(OracleUserStorageProvider.class);
    private final KeycloakSession session;
    private final ComponentModel model;
    private final UserRepository userRepository;

    public OracleUserStorageProvider(final KeycloakSession session, final ComponentModel model, final UserRepository userRepository) {
        this.session = session;
        this.model = model;
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(final RealmModel realm, final UserModel user, final CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        final UserCredentialModel cred = (UserCredentialModel) input;
        final boolean valid =
                this.userRepository.validateCredentials(user.getUsername(), cred.getValue());
        LOGGER.info("User " + user.getUsername() + " is " + ((valid) ? "valid" : "invalid"));
        return valid;
    }

    @Override
    public boolean updateCredential(final RealmModel realm, final UserModel user, final CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }
        final UserCredentialModel cred = (UserCredentialModel) input;
        LOGGER.info("Updating user " + user.getUsername() + " credentials...");
        final User updateCredentials = this.userRepository.updateCredentials(user.getUsername(), cred.getValue());
        final boolean updated = updateCredentials != null;
        if (updated) {
            getUserAdapter(user).setPassword(updateCredentials.getPassword());
            LOGGER.info("Credentials was successfully updated");
        }
        return updated;
    }

    @Override
    public UserModel addUser(final RealmModel realm, final String username) {
        if (username != null && !ADMIN.equalsIgnoreCase(username)) {
            final User user = this.userRepository.save(username);
            LOGGER.info("Saving user " + username + " ...");
            return new UserAdapter(session, realm, model, user);
        } else {
            return null;
        }
    }

    @Override
    public boolean removeUser(final RealmModel realm, final UserModel user) {
        final String externalId = StorageId.externalId(user.getId());
        LOGGER.info("Removing user by ID: " + externalId);
        final boolean remove = this.userRepository.remove(externalId);
        if (remove) {
            LOGGER.info("User with ID " + externalId + " was successfully removed");
        }
        return remove;
    }

    @Override
    public int getUsersCount(final RealmModel realm) {
        final int usersCount = this.userRepository.getUsersCount();
        LOGGER.info("Users Count: " + usersCount);
        return usersCount;
    }

    @Override
    public List<UserModel> getUsers(final RealmModel realm) {
        final List<UserModel> users =
                this.userRepository.findAll().stream()
                        .map(user -> new UserAdapter(session, realm, model, user))
                        .collect(Collectors.toList());
        LOGGER.info("Returned " + users.size() + " users");
        return users;
    }

    @Override
    public List<UserModel> getUsers(
            final RealmModel realm, final int firstResult, final int maxResults) {
        final List<UserModel> users =
                this.userRepository.findAll(firstResult, maxResults).stream()
                        .map(user -> new UserAdapter(session, realm, model, user))
                        .collect(Collectors.toList());
        LOGGER.info("Returned " + users.size() + " users");
        return users;
    }

    @Override
    public List<UserModel> searchForUser(
            Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        Collection<User> users = new ArrayList<User>();
        if (params.isEmpty()) {
            users = this.userRepository.findAll();
        } else if (params.containsKey(USERS_QUERY_SEARCH)) {
            final String search = params.get(USERS_QUERY_SEARCH);
            users = this.userRepository.findAllByUsernameOrEmail(search, firstResult, maxResults);
        }
        final List<UserModel> usersModel = users.stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
        LOGGER.info("Returned " + users.size() + " users");
        return usersModel;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        Collection<User> users = new ArrayList<User>();
        if (params.isEmpty()) {
            users = this.userRepository.findAll();
        } else if (params.containsKey(USERS_QUERY_SEARCH)) {
            final String search = params.get(USERS_QUERY_SEARCH);
            users = this.userRepository.findAllByUsernameOrEmail(search);
        }
        final List<UserModel> usersModel = users.stream()
                .map(user -> new UserAdapter(session, realm, model, user))
                .collect(Collectors.toList());
        LOGGER.info("Returned " + users.size() + " users");
        return usersModel;
    }

    @Override
    public List<UserModel> searchForUser(final String search, final RealmModel realm) {
        final List<UserModel> users =
                this.userRepository.findAllByUsernameOrEmail(search).stream()
                        .map(user -> new UserAdapter(session, realm, model, user))
                        .collect(Collectors.toList());
        LOGGER.info("Returned " + users.size() + " users");
        return users;
    }

    @Override
    public List<UserModel> searchForUser(
            final String search, final RealmModel realm, final int firstResult, final int maxResults) {
        final List<UserModel> users =
                this.userRepository.findAllByUsernameOrEmail(search, firstResult, maxResults).stream()
                        .map(user -> new UserAdapter(session, realm, model, user))
                        .collect(Collectors.toList());
        LOGGER.info("Returned " + users.size() + " users");
        return users;
    }

    @Override
    public UserModel getUserById(final String id, final RealmModel realm) {
        final String externalId = StorageId.externalId(id);
        LOGGER.info("ID: " + id + ", External Id: " + externalId);
        final User user = this.userRepository.findUserByUsername(externalId);
        if (user == null) {
            LOGGER.warn("User with ID " + id + " not found");
            return null;
        } else {
            LOGGER.info("User " + user.getUsername() + " found by id " + externalId);

            return new UserAdapter(session, realm, model, user);
        }
    }

    @Override
    public UserModel getUserByUsername(final String username, final RealmModel realm) {
        final User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.warn("User with username " + username + " not found");
            return null;
        } else {
            LOGGER.info("User " + user.getUsername() + " found by username " + username);

            return new UserAdapter(session, realm, model, user);
        }
    }

    @Override
    public UserModel getUserByEmail(final String email, final RealmModel realm) {
        final User user = this.userRepository.findUserByEmail(email);
        if (user == null) {
            LOGGER.warn("User with email " + email + " not found");
            return null;
        } else {
            LOGGER.info("User " + user.getUsername() + " found by email " + email);

            return new UserAdapter(session, realm, model, user);
        }
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
    public boolean supportsCredentialType(final String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCache(
            final RealmModel realm, final CachedUserModel user, final UserModel delegate) {
        final String password = ((UserAdapter) delegate).getPassword();
        if (password != null) {
            user.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }
    }

    @Override
    public void disableCredentialType(
            final RealmModel realm, final UserModel user, final String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return;
        }
        getUserAdapter(user).setPassword(null);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(final RealmModel realm, final UserModel user) {
        if (getUserAdapter(user).getPassword() != null) {
            final Set<String> set = new HashSet<>();
            set.add(PasswordCredentialModel.TYPE);
            return set;
        } else {
            return Collections.emptySet();
        }
    }

    private UserAdapter getUserAdapter(final UserModel user) {
        return (user instanceof CachedUserModel)
                ? (UserAdapter) ((CachedUserModel) user).getDelegateForUpdate()
                : (UserAdapter) user;
    }

    @Override
    public void close() {
        LOGGER.debug("Closing " + OracleUserStorageProvider.class.getSimpleName() + "...");
    }
}

package com.redhat.rhsso.spi.config;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.storage.UserStorageProviderModel;

import java.util.List;

public class UserFederationConfig {

    private final MultivaluedHashMap<String, String> configuration;

    public UserFederationConfig(UserStorageProviderModel providerModel) {
        this(providerModel.getConfig());
    }

    public UserFederationConfig(ComponentModel providerModel) {
        this(providerModel.getConfig());
    }

    public UserFederationConfig(MultivaluedHashMap<String, String> config) {
        this.configuration = config;
    }

    public MultivaluedHashMap<String, String> configmap() {
        return configuration;
    }

    private String get(List<String> values, String defaultValue) {
        String value = values!=null?values.get(0):null;
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return value;
    }

    // Constants Getters and Setters
    public String passwordColumn() {
        return get(configmap().get(Constants.PASSWORD_COLUMN), Constants.DEFAULT_PASSWORD_COLUMN);
    }

    public Integer passwordMinLength() {
        return Integer.valueOf(get(configmap().get(Constants.AUTH_MIN_REQUIRED_PASSWORD_LENGTH), Constants.DEFAULT_AUTH_MIN_REQUIRED_PASSWORD_LENGTH));
    }

    public Integer passwordMaxLength() {
        return Integer.valueOf(get(configmap().get(Constants.AUTH_MAX_REQUIRED_PASSWORD_LENGTH), Constants.DEFAULT_AUTH_MAX_REQUIRED_PASSWORD_LENGTH));
    }

    public Integer passwordMaxRepeatedCharsLength() {
        return Integer.valueOf(get(configmap().get(Constants.AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH), Constants.DEFAULT_AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH));
    }

    public Boolean userRegistrationEnabled() {
        return Boolean.valueOf(get(configmap().get(Constants.AUTH_USER_REGISTRATION), Constants.DEFAULT_AUTH_USER_REGISTRATION));
    }

    public String queryUserByUsername() {
        return get(configmap().get(Constants.SQL_QUERY_USER_BY_USERNAME), Constants.DEFAULT_SQL_QUERY_USER_BY_USERNAME);
    }

    public String queryUserByEmail() {
        return get(configmap().get(Constants.SQL_QUERY_USER_BY_EMAIL), Constants.DEFAULT_SQL_QUERY_USER_BY_EMAIL);
    }

    public String queryUserCount() {
        return get(configmap().get(Constants.SQL_QUERY_USER_COUNT), Constants.DEFAULT_SQL_QUERY_USER_COUNT);
    }

    public String queryAllUsers() {
        return get(configmap().get(Constants.SQL_QUERY_ALL_USERS), Constants.DEFAULT_SQL_QUERY_ALL_USERS);
    }

    public String querySearchForUser() {
        return get(configmap().get(Constants.SQL_QUERY_SEARCH_USER), Constants.DEFAULT_SQL_QUERY_SEARCH_USER);
    }


    @Override
    public String toString() {
        return "UserFederationConfig{" +
                "configuration=" + configuration +
                '}';
    }
}

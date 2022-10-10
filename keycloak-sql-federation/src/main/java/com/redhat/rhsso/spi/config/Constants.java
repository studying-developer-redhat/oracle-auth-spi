package com.redhat.rhsso.spi.config;

import org.keycloak.provider.ProviderConfigProperty;
import java.util.*;

public class Constants {

    static Map<String,String> defaults = new HashMap<>();

    // General properties
    final static String PASSWORD_COLUMN = "JPQL password column name";
    // Security properties
    final static String AUTH_MIN_REQUIRED_PASSWORD_LENGTH = "Security - Minimum password length";
    final static String AUTH_MAX_REQUIRED_PASSWORD_LENGTH = "Security - Maximum password length";
    final static String AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH = "Security - Maximum repeated characters length";
    final static String AUTH_USER_REGISTRATION = "Security - Enable user registration?";
    // Hibernate SQL properties
    final static String SQL_QUERY_USER_BY_USERNAME = "Hibernate NamedQuery - UserByUsername";
    final static String SQL_QUERY_USER_BY_EMAIL = "Hibernate NamedQuery - UserByEmail";
    final static String SQL_QUERY_USER_COUNT = "Hibernate NamedQuery - UserCount";
    final static String SQL_QUERY_ALL_USERS = "Hibernate NamedQuery - AllUsers";
    final static String SQL_QUERY_SEARCH_USER = "Hibernate NamedQuery - searchForUser";
    // Default Constant Values
    final static String DEFAULT_PASSWORD_COLUMN = "password";
    final static String DEFAULT_AUTH_MIN_REQUIRED_PASSWORD_LENGTH = "6";
    final static String DEFAULT_AUTH_MAX_REQUIRED_PASSWORD_LENGTH = "20";
    final static String DEFAULT_AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH = "3";
    final static String DEFAULT_AUTH_USER_REGISTRATION = "true";
    final static String DEFAULT_SQL_QUERY_USER_BY_USERNAME = "select u from User u where lower(u.username) = lower(:username)";
    final static String DEFAULT_SQL_QUERY_USER_BY_EMAIL = "select u from User u where lower(u.email) = lower(:email)";
    final static String DEFAULT_SQL_QUERY_USER_COUNT = "select count(u.id) from User u";
    final static String DEFAULT_SQL_QUERY_ALL_USERS = "select u from User u";
    final static String DEFAULT_SQL_QUERY_SEARCH_USER = "select u from User u where ( lower(u.username) like :search or lower(u.email) like :search ) order by u.username";
    final static String DEFAULT_DEBUG = "true";
    final static String DEFAULT_DEBUG_SHOW_SQL = "true";
    final static String DEFAULT_DEBUG_FORMAT_SQL = "true";
    final static String DEFAULT_DEBUG_SQL_COMMENTS = "true";

    public static String getDefaultConfig(String key) {
        return defaults.get(key);
    }

    static {
        defaults.put(PASSWORD_COLUMN, DEFAULT_PASSWORD_COLUMN);
        defaults.put(AUTH_MIN_REQUIRED_PASSWORD_LENGTH, DEFAULT_AUTH_MIN_REQUIRED_PASSWORD_LENGTH);
        defaults.put(AUTH_MAX_REQUIRED_PASSWORD_LENGTH, DEFAULT_AUTH_MAX_REQUIRED_PASSWORD_LENGTH);
        defaults.put(AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH, DEFAULT_AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH);
        defaults.put(AUTH_USER_REGISTRATION, DEFAULT_AUTH_USER_REGISTRATION);
        defaults.put(SQL_QUERY_USER_BY_USERNAME, DEFAULT_SQL_QUERY_USER_BY_USERNAME);
        defaults.put(SQL_QUERY_USER_BY_EMAIL, DEFAULT_SQL_QUERY_USER_BY_EMAIL);
        defaults.put(SQL_QUERY_USER_COUNT, DEFAULT_SQL_QUERY_USER_COUNT);
        defaults.put(SQL_QUERY_ALL_USERS, DEFAULT_SQL_QUERY_ALL_USERS);
        defaults.put(SQL_QUERY_SEARCH_USER, DEFAULT_SQL_QUERY_SEARCH_USER);
    }

    public static Set<ProviderConfigProperty> getConfigurationOptions() {
        return new LinkedHashSet<>(Arrays.asList(
                new ProviderConfigProperty(AUTH_MIN_REQUIRED_PASSWORD_LENGTH, AUTH_MIN_REQUIRED_PASSWORD_LENGTH, "Minimum required password length (default to 6 characters)", ProviderConfigProperty.STRING_TYPE, DEFAULT_AUTH_MIN_REQUIRED_PASSWORD_LENGTH),
                new ProviderConfigProperty(AUTH_MAX_REQUIRED_PASSWORD_LENGTH, AUTH_MAX_REQUIRED_PASSWORD_LENGTH, "Maximum required password length (default to 20 characters)", ProviderConfigProperty.STRING_TYPE, DEFAULT_AUTH_MAX_REQUIRED_PASSWORD_LENGTH),
                new ProviderConfigProperty(AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH, AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH, "Maximum repeated characters length (default to 3 characters)", ProviderConfigProperty.STRING_TYPE, DEFAULT_AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH),
                new ProviderConfigProperty(SQL_QUERY_USER_BY_USERNAME, SQL_QUERY_USER_BY_USERNAME, "Hibernate Query for getUserByUsername operation", ProviderConfigProperty.STRING_TYPE, DEFAULT_SQL_QUERY_USER_BY_USERNAME),
                new ProviderConfigProperty(SQL_QUERY_USER_BY_EMAIL, SQL_QUERY_USER_BY_EMAIL, "Hibernate Query for getUserByEmail operation", ProviderConfigProperty.STRING_TYPE, DEFAULT_SQL_QUERY_USER_BY_EMAIL),
                new ProviderConfigProperty(SQL_QUERY_USER_COUNT, SQL_QUERY_USER_COUNT, "Hibernate Query for getUserCount operation", ProviderConfigProperty.STRING_TYPE, DEFAULT_SQL_QUERY_USER_COUNT),
                new ProviderConfigProperty(SQL_QUERY_ALL_USERS, SQL_QUERY_ALL_USERS, "Hibernate Query for getAllUsers operation", ProviderConfigProperty.STRING_TYPE, DEFAULT_SQL_QUERY_ALL_USERS),
                new ProviderConfigProperty(SQL_QUERY_SEARCH_USER, SQL_QUERY_SEARCH_USER, "Hibernate Query for searchForUser operation", ProviderConfigProperty.STRING_TYPE, DEFAULT_SQL_QUERY_SEARCH_USER),
                new ProviderConfigProperty(AUTH_USER_REGISTRATION, AUTH_USER_REGISTRATION, "Enable user registration?", ProviderConfigProperty.BOOLEAN_TYPE, DEFAULT_AUTH_USER_REGISTRATION)
        ));
    }

    public static Set<ProviderValidationProperty> getValidationOptions() {
        return new LinkedHashSet<>(Arrays.asList(
                new ProviderValidationProperty(AUTH_MIN_REQUIRED_PASSWORD_LENGTH, "User minimum required password length must not be null", false),
                new ProviderValidationProperty(AUTH_MAX_REQUIRED_PASSWORD_LENGTH, "User maximum required password length must not be null", false),
                new ProviderValidationProperty(AUTH_MAX_REPEATED_CHARS_PASSWORD_LENGTH, "User maximum repeated password characters length must not be null", false)
        ));
    }

}

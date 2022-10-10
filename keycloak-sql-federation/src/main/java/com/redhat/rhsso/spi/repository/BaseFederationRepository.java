package com.redhat.rhsso.spi.repository;

import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class BaseFederationRepository<T> implements GenericRepository<T> {

    private Class<T> type;
    protected EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public BaseFederationRepository() {
        Type t = getClass().getGenericSuperclass();
        while (!(t instanceof ParameterizedType) || ((ParameterizedType) t).getRawType() != BaseFederationRepository.class) {
            if (t instanceof ParameterizedType) {
                t = ((Class<?>) ((ParameterizedType) t).getRawType()).getGenericSuperclass();
            } else {
                t = ((Class<?>) t).getGenericSuperclass();
            }
        }
        this.type = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
    }

    public T getUserById(Long id) {
        return null;
    }

    public T getUserByUsername(String username) {
        return null;
    }

    public T getUserByEmail(String email) {
        return null;
    }

    public int getUsersCount() {
        return 0;
    }

    public List<UserModel> getUsers(Integer firstResult, Integer maxResults, RealmModel realm) {
        return null;
    }

    public List<UserModel> searchForUserByUsernameOrEmail(String search, Integer firstResult, Integer maxResults, RealmModel realm) {
        return null;
    }

    public UserModel addUser(String username, RealmModel realm) {
        return null;
    }

    public Boolean removeUser(String externalId) {
        return null;
    }

    public Boolean updateUser(T entity) {
        return null;
    }

    public Boolean isValidPassword(String credential) {
        return null;
    }
}

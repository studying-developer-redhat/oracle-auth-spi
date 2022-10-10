package com.redhat.rhsso.spi.repository;

import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import java.util.List;

public interface GenericRepository<T>{

	T getUserById(Long id);

	T getUserByUsername(String username);

	T getUserByEmail(String email);

	int getUsersCount();

	List<UserModel> getUsers(Integer firstResult, Integer maxResults, RealmModel realm);

	List<UserModel> searchForUserByUsernameOrEmail(String search, Integer firstResult, Integer maxResults, RealmModel realm);

	UserModel addUser(String username, RealmModel realm);

	Boolean removeUser(String externalId);

	Boolean updateUser(T entity);

	Boolean isValidPassword(String credential);
}


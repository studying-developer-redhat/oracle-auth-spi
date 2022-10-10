package com.redhat.rhsso.spi.adapter;

import com.redhat.rhsso.spi.OracleUserStorageProvider;
import com.redhat.rhsso.spi.model.User;
import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    protected static final Logger logger = Logger.getLogger(UserAdapter.class);

    protected User entity;

    protected String keycloakId;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, User entity) {
        super(session, realm, model);
        this.entity = entity;
        this.keycloakId = StorageId.keycloakId(model, String.valueOf(getEntity().getId()));
    }

    public User getEntity() {
        return this.entity;
    }

    public String getPassword() {
        return getEntity().getPassword();
    }

    public void setPassword(String password) {
        getEntity().setPassword(password);
    }

    @Override
    public String getUsername() {
        return getEntity().getUsername();
    }

    @Override
    public void setUsername(String username) {
        getEntity().setUsername(username);
    }

    @Override
    public void setEmail(String email) {
        getEntity().setEmail(email);
    }

    @Override
    public String getEmail() {
        return getEntity().getEmail();
    }

    @Override
    public String getId() {
        return this.keycloakId;
    }

    @Override
    public String getFirstName() {
        return getEntity().getPerson().getName();
    }

    @Override
    public String getLastName() {
        return getEntity().getPerson().getFamily();
    }

    @Override
    public void setFirstName(String firstName) {
        getEntity().getPerson().setName(firstName);
    }

    @Override
    public void setLastName(String lastName) {
        getEntity().getPerson().setFamily(lastName);
    }

    @Override
    public  void setSingleAttribute(String name, String value) {
        super.setSingleAttribute(name, value);
    }

    @Override
    public  void removeAttribute(String name) {
        super.removeAttribute(name);
    }

    @Override
    public  void setAttribute(String name, List<String> values) {
        super.setAttribute(name, values);
    }

    @Override
    public  String getFirstAttribute(String name) {
        return super.getFirstAttribute(name);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();

        // all.putAll(attrs);

        return all;
    }

    @Override
    public  List<String> getAttribute(String name) {
        return super.getAttribute(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserAdapter that = (UserAdapter) o;
        return getEntity().equals(that.getEntity()) &&
                keycloakId.equals(that.keycloakId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEntity(), keycloakId);
    }

    @Override
    public String toString() {
        return "UserAdapter [user=" + getEntity() + ", keycloakId=" + this.keycloakId + "]";
    }
}

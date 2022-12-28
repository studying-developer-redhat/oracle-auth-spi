package com.redhat.rhsso.spi.adapter;

import com.redhat.rhsso.spi.base.AbstractUserAdapter;
import com.redhat.rhsso.spi.model.entity.User;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode(callSuper = false, of = { "user", "keycloakId" })
public class UserAdapter extends AbstractUserAdapter {

    private static final String PERSON_ID_ATTRIBUTE = "personId";

    private final User user;
    private final String keycloakId;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, User user) {
        super(session, realm, model);
        this.user = user;
        this.keycloakId = StorageId.keycloakId(model, user.getUsername());
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        user.setUsername(username);
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        user.setEmail(email);
    }

    public String getPassword() {
        return user.getPassword();
    }

    public void setPassword(final String password) {
        this.user.setPassword(password);
    }

    @Override
    public  void removeAttribute(String name) {
        super.removeAttribute(name);
    }


    @Override
    public  String getFirstAttribute(String name) {
        return super.getFirstAttribute(name);
    }

    @Override
    public void addUserAttributes(Map<String, List<String>> attributes) {
        if (user.getUsername() != null) {
            attributes.put(PERSON_ID_ATTRIBUTE, Arrays.asList(user.getPerson().getId().toString()));
        }
    }

    @Override
    public void setSingleAttribute(final String name, final String value) {
        if (PERSON_ID_ATTRIBUTE.equalsIgnoreCase(name)) {
            user.getPerson().setId(Long.valueOf(value));
        }
    }

    @Override
    public void setAttribute(final String name, final List<String> values) {
        if (!values.isEmpty()) {
            setSingleAttribute(name, values.get(0));
        }
    }
}

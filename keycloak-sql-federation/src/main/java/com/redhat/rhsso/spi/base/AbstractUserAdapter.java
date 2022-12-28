package com.redhat.rhsso.spi.base;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import javax.ws.rs.core.MultivaluedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractUserAdapter extends AbstractUserAdapterFederatedStorage {

    public AbstractUserAdapter(final KeycloakSession session, final RealmModel realm,
                               final ComponentModel storageProviderModel) {
        super(session, realm, storageProviderModel);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        final Map<String, List<String>> attrs = super.getAttributes();
        final MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        addUserAttributes(all);
        return all;
    }

    public abstract void addUserAttributes(final Map<String, List<String>> attributes);

    @Override
    public List<String> getAttribute(final String name) {
        final Map<String, List<String>> attributes = getAttributes();
        final List<String> attribute = attributes.get(name);
        return (attribute == null) ? new ArrayList<>() : attribute;
    }

}
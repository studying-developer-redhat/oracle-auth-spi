package com.redhat.rhsso.spi;

import com.redhat.rhsso.spi.config.Constants;
import com.redhat.rhsso.spi.config.ProviderValidationProperty;
import com.redhat.rhsso.spi.config.UserFederationConfig;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.storage.UserStorageProviderFactory;
import org.keycloak.storage.UserStorageProviderModel;
import javax.naming.InitialContext;
import java.util.*;

public class OracleUserStorageProviderFactory implements UserStorageProviderFactory<OracleUserStorageProvider> {

    protected static final Logger logger = Logger.getLogger(OracleUserStorageProviderFactory.class);
    protected static List<ProviderConfigProperty> configMetadata = null;
    private static final String PROVIDER_ID = "oracle-storage-spi";

    @Override
    public OracleUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        try {
            logger.info("initializing OracleUserStorageProviderFactory");
            InitialContext ctx = new InitialContext();
            OracleUserStorageProvider provider = (OracleUserStorageProvider)ctx.lookup("java:global/keycloak-sql-federation/" + OracleUserStorageProvider.class.getSimpleName());
            UserFederationConfig config = new UserFederationConfig((UserStorageProviderModel) model);

            if (config == null) {
                System.out.println("config is null in OracleUserStorageProviderFactory");
            }else {
                System.out.println("config set properly in OracleUserStorageProviderFactory");
            }

            provider.setModel(model);
            provider.setSession(session);
            provider.setConfig(config);
            return provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<ProviderConfigProperty> getConfigurationOptions() {
        return new LinkedHashSet<>(Constants.getConfigurationOptions());
    }

    public Set<ProviderValidationProperty> getValidationOptions() {
        return new LinkedHashSet<>(Constants.getValidationOptions());
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel config) {
        UserFederationConfig conf = new UserFederationConfig(config);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        if(configMetadata==null) {
            return new ArrayList<>(getConfigurationOptions());
        }
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config)
            throws ComponentValidationException {
        for (ProviderValidationProperty pvp : getValidationOptions()) {
            String value = config.getConfig().getFirst(pvp.getName());
            logger.infof("SPI validation for attribute {}", pvp.getName());

            if (value == null) {
                value = "";
                logger.infof("SPI validation for attribute {} : set with empty value", pvp.getName());
            }

            if (!pvp.allowNull() && "".equals(value.trim())) {
                throw new ComponentValidationException(pvp.getMessage());
            } else if (pvp.getRegex() != null && "".equals(value.trim())) {
                throw new ComponentValidationException(pvp.getMessage());
            } else if (pvp.getRegex() != null && !pvp.isRespectingRegex(value)) {
                throw new ComponentValidationException(pvp.getMessage());
            }

        }
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void close() {
        logger.info("closing user-storage-spi factory");
    }

    @Override
    public String getHelpText() {
        return "JPA Example User Storage Provider";
    }
}

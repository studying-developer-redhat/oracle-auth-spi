package com.redhat.rhsso.spi;

import com.redhat.rhsso.spi.repository.impl.UserRepository;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class OracleUserStorageProviderFactory implements UserStorageProviderFactory<OracleUserStorageProvider> {

    private static final String USER_STORAGE_FACTORY_NAME = "oracle-user-storage-factory-spi";
    private static final String SPI_JNDI_NAME = "keycloak-sql-federation";
    private static final Logger LOGGER = Logger.getLogger(OracleUserStorageProviderFactory.class);

    @Override
    public OracleUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        OracleUserStorageProvider storageProvider;
        LOGGER.info("################# start create() #################");
        LOGGER.info("Creating User Storage Provider...");
        try {

            InitialContext ctx = new InitialContext();
            final UserRepository userRepository =  (UserRepository)  ctx.lookup(String.format("java:global/%s/%s",
                    SPI_JNDI_NAME, UserRepository.class.getSimpleName()));
            storageProvider = new OracleUserStorageProvider(session, model, userRepository);
        } catch (final NamingException e) {
            throw new RuntimeException("Error on creating User Storage Provider: " + e.getMessage(), e);
        }
        LOGGER.info("################# end create() #################");
        LOGGER.info("Created User Storage Provider...");
        return storageProvider;
    }

    @Override
    public String getId() {
        return USER_STORAGE_FACTORY_NAME;
    }
}

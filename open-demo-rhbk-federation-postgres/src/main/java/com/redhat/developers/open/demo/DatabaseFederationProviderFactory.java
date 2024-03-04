package com.redhat.developers.open.demo;

import java.util.List;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseFederationProviderFactory implements UserStorageProviderFactory<DatabaseFederationProvider> {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void close() {
        logger.info("Closing FederationDB Factory...");
    }

    @Override
    public void init(Config.Scope arg0) {
        logger.info("Creating FederationProvider Factory...");
    }

    @Override
    public void postInit(KeycloakSessionFactory arg0) {
        logger.info("Finish creating FederationProvider Factory...");
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                .property("jdbcURL", "JDBC URL to Database", "JDBC URL",
                        ProviderConfigProperty.STRING_TYPE, "jdbc:postgresql://host:port/database", null)
                .property("username", "Database Username", "Database Username", ProviderConfigProperty.STRING_TYPE, "postgres", null)
                .property("password", "Database Password", "Database Password", ProviderConfigProperty.STRING_TYPE, "postgres", null)
                .property("Passwd_query", "Query to get Password by Email",
                        "Query to get Password by Email",
                        ProviderConfigProperty.STRING_TYPE, "select password from users where email=?",
                        null)
                .property("User_query", "Query to get User Data",
                        "Query to get User Data",
                        ProviderConfigProperty.STRING_TYPE,
                        "select email,name as firstName from users where email=?",
                        null)
                .build();
    }

    @Override
    public String getHelpText() {
        return "Database Federation Provider";
    }

    @Override
    public DatabaseFederationProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new DatabaseFederationProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "Database Federation Provider";
    }
}

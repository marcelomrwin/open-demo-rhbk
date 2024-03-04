package com.redhat.developers.open.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import com.redhat.developers.open.demo.model.UserData;
import com.redhat.developers.open.demo.model.UserDatabase;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseFederationProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private KeycloakSession session;
    private ComponentModel model;
    private UserModel userData;
    private UserDatabase userDb;
    private Logger logger = LoggerFactory.getLogger(getClass());


    public DatabaseFederationProvider(KeycloakSession sess, ComponentModel model) {
        this.session = sess;
        this.model = model;
    }

    @Override
    public boolean supportsCredentialType(String s) {
        return false;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String jdbcURL = model.getConfig().getFirst("jdbcURL");
            String passwdQuery = model.getConfig().getFirst("Passwd_query");
            String username = model.getConfig().getFirst("username");
            String password = model.getConfig().getFirst("password");
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");

            logger.info("Getting Connection to {}", jdbcURL);
            connection = DriverManager.getConnection(jdbcURL, props);
            logger.info("connection made successfully");

            preparedStatement = connection.prepareStatement(passwdQuery);
            preparedStatement.setString(1, userModel.getEmail());

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            String passwdDb = rs.getString("password");

            if (credentialInput.getChallengeResponse().equals(passwdDb)) {
                logger.info("Password Matched for:" + userModel.getEmail());
                return true;
            } else {
                logger.info("Password Not Matched for:" + userModel.getEmail());
                return false;
            }
        } catch (Exception e) {
            logger.error("Password Validation Error", e);
            return false;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void close() {
        logger.info("Closing FederationDB Provider");
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String s) {
        logger.info("getUserById {}", s);
        return getUserByEmail(realmModel, StorageId.externalId(s));
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String s) {
        logger.info("getUserByUsername {}", s);
        return getUserByEmail(realmModel, s);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        logger.warn("Get User By Email: {}", email);
        try {

            UserModel local = session.users().getUserByEmail(realm, email);

            if (local!=null){
                logger.warn("User local founded!! {}",local.getId());return local;
            }

            String jdbcURL = model.getConfig().getFirst("jdbcURL");
            String userQuery = model.getConfig().getFirst("User_query");
            String username = model.getConfig().getFirst("username");
            String password = model.getConfig().getFirst("password");
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("ssl", "false");

            logger.info("Getting Connection to {}", jdbcURL);
            connection = DriverManager.getConnection(jdbcURL, props);
            logger.info("connection made successfully");

            preparedStatement = connection.prepareStatement(userQuery);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) {
                logger.info("Email: " + email + " not found");
                return null;
            }

            String dbEmail = rs.getString("email");
            UserModel userData = session.users().addUser(realm, dbEmail);
            logger.warn("User created with link {}",userData.getFederationLink());
            userData.setEmail(dbEmail);
            userData.setUsername(dbEmail);
            userData.setFirstName(rs.getString("firstName"));
            userData.setCreatedTimestamp(System.currentTimeMillis());
            userData.setEnabled(true);
            userData.setEmailVerified(true);

            return userData;

        } catch (Exception e) {
            logger.error("Error getting user with email: " + email, e);
            return null;
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                logger.error("unexpected exception", e);
            }

        }
    }

}

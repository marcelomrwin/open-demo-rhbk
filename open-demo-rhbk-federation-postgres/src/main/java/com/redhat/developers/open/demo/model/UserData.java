package com.redhat.developers.open.demo.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class UserData extends AbstractUserAdapterFederatedStorage implements UserModel {
    private ComponentModel model;
    private UserDatabase userDb;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
    private String userId;
    private String email;
    private String userName;
    private String role;
    private boolean enabled;

    public UserData(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel) {
        super(session, realm, storageProviderModel);
        model = storageProviderModel;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public void setUsername(String s) {
        this.userName = s;
    }

    public ComponentModel getModel() {
        return model;
    }

    public void setModel(ComponentModel model) {
        this.model = model;
    }

    public UserDatabase getUserDb() {
        return userDb;
    }

    public void setUserDb(UserDatabase userDb) {
        this.userDb = userDb;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean isEmailVerified() {
        return emailVerified;
    }

    @Override
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Map<String, List<String>> getAttributes() {

        Map<String, List<String>> map1 = new HashMap<String, List<String>>();
        List<String> list1 = new LinkedList<String>();
        list1.add("from External DB");
        map1.put("attribute1", list1);

        return map1;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

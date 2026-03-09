package com.gamecatarellibarber.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private Map<String, String> users = new HashMap<>();
    private Map<String, String> roles = new HashMap<>();

    public Map<String, String> getUsers() {
        return users;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users != null ? users : new HashMap<>();
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, String> roles) {
        this.roles = roles != null ? roles : new HashMap<>();
    }

    public String getRole(String username) {
        return roles.getOrDefault(username, "BARBER");
    }
}

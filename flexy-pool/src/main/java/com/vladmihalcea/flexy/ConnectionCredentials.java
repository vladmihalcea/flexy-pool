package com.vladmihalcea.flexy;

/**
 * ConnectionCredentials - Holds connection credentials
 *
 * @author Vlad Mihalcea
 */
public class ConnectionCredentials {

    private final String username;
    private final String password;

    public ConnectionCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

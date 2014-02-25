package com.vladmihalcea.flexy;

/**
 * Credentials - Holds connection credentials
 *
 * @author Vlad Mihalcea
 */
public class Credentials {

    private final String username;
    private final String password;

    public Credentials(String username, String password) {
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

package com.vladmihalcea.flexy.connection;

/**
 * <code>Credentials</code> stores the connection authentication info.
 *
 * @author	Vlad Mihalcea
 * @version	%I%, %E%
 * @since	1.0
 */
public class Credentials {

    private final String username;
    private final String password;

    /**
     * Set both username and password
     * @param username username
     * @param password password
     */
    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }
}

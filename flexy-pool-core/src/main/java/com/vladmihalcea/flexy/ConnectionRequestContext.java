package com.vladmihalcea.flexy;

/**
 * ConnectionRequestContext - Context holder for a connection request
 *
 * @author Vlad Mihalcea
 */
public class ConnectionRequestContext {

    private final Credentials credentials;
    private int attempts;

    public ConnectionRequestContext(Credentials credentials) {
        this.credentials = credentials;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public static class Builder {

        private Credentials credentials;

        public Builder setCredentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public ConnectionRequestContext build() {
            return new ConnectionRequestContext(
                credentials
            );
        }
    }
}

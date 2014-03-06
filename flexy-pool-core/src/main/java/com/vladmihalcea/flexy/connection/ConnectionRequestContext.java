package com.vladmihalcea.flexy.connection;

/**
 * ConnectionRequestContext - Context holder for a connection request
 *
 * @author Vlad Mihalcea
 */
public final class ConnectionRequestContext {

    private final Credentials credentials;
    private int retryAttempts;
    private int overflowPoolSize;

    private ConnectionRequestContext(Credentials credentials) {
        this.credentials = credentials;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public int getOverflowPoolSize() {
        return overflowPoolSize;
    }

    public void incrementAttempts() {
        this.retryAttempts++;
    }

    public void incrementOverflowPoolSize() {
        this.overflowPoolSize++;
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

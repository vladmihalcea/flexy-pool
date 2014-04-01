package com.vladmihalcea.flexypool.connection;

/**
 * <code>ConnectionRequestContext</code> stores all info required for retrieving one connection and data
 * generated during any attempt for getting such connection.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public final class ConnectionRequestContext {

    /**
     * Factory for creating ConnectionRequestContext instances.
     */
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

    private final Credentials credentials;
    private int retryAttempts;

    private ConnectionRequestContext(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Get current connection credentials
     *
     * @return connection credentials
     */
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Get current connection retry attempts
     *
     * @return current connection retry attempts
     */
    public int getRetryAttempts() {
        return retryAttempts;
    }

    /**
     * Increment the retry attempts number
     */
    public void incrementAttempts() {
        this.retryAttempts++;
    }

    @Override
    public String toString() {
        return "ConnectionRequestContext{" +
                "credentials=" + credentials +
                ", retryAttempts=" + retryAttempts +
                '}';
    }
}

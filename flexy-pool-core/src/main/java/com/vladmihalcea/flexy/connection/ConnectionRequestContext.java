package com.vladmihalcea.flexy.connection;

import com.vladmihalcea.flexy.config.FlexyConfiguration;

/**
 * ConnectionRequestContext - Context holder for a connection request
 *
 * @author Vlad Mihalcea
 */
public class ConnectionRequestContext {

    private final FlexyConfiguration configuration;
    private final Credentials credentials;
    private int retryAttempts;
    private int overflowPoolSize;

    private ConnectionRequestContext(FlexyConfiguration configuration, Credentials credentials) {
        this.configuration = configuration;
        this.credentials = credentials;
    }

    public FlexyConfiguration getConfiguration() {
        return configuration;
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

        private final FlexyConfiguration configuration;
        private Credentials credentials;

        public Builder(FlexyConfiguration configuration) {
            this.configuration = configuration;
        }

        public Builder setCredentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public ConnectionRequestContext build() {
            return new ConnectionRequestContext(
                configuration,
                credentials
            );
        }
    }
}

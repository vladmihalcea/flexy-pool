package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionFactory;

/**
 * AbstractConnectionAcquiringStrategy - Abstract base class for all connection acquiring strategies
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final Configuration configuration;
    private final ConnectionFactory connectionFactory;

    protected AbstractConnectionAcquiringStrategy(Configuration configuration, ConnectionAcquiringStrategy connectionAcquiringStrategy) {
        this.configuration = configuration;
        this.connectionFactory = connectionAcquiringStrategy != null ? connectionAcquiringStrategy : configuration.getPoolAdapter();
    }

    protected AbstractConnectionAcquiringStrategy(Configuration configuration) {
        this(configuration, null);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Get the connection factory.
     * @return connection factory
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}

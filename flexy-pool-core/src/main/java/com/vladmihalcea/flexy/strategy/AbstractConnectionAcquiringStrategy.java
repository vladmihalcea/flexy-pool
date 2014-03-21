package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionFactory;

/**
 * <code>AbstractConnectionAcquiringStrategy</code> implements the {@link ConnectionAcquiringStrategy} adding
 * the configuration and connectionFactory properties.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final Configuration configuration;
    private final ConnectionFactory connectionFactory;

    /**
     * Creates a strategy using the given {@link com.vladmihalcea.flexy.config.Configuration}
     * @param configuration configuration
     */
    protected AbstractConnectionAcquiringStrategy(Configuration configuration) {
        this.configuration = configuration;
        this.connectionFactory = configuration.getPoolAdapter();
    }

    /**
     * @return configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * @return connectionFactory
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}

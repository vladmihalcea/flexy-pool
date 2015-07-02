package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.connection.ConnectionFactory;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * <code>AbstractConnectionAcquiringStrategy</code> implements the {@link ConnectionAcquiringStrategy} adding
 * the configuration and connectionFactory properties.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final ConfigurationProperties configurationProperties;
    private final ConnectionFactory connectionFactory;

    /**
     * Creates a strategy using the given {@link com.vladmihalcea.flexypool.util.ConfigurationProperties}
     *
     * @param configurationProperties configurationProperties
     */
    protected AbstractConnectionAcquiringStrategy(ConfigurationProperties<? extends DataSource, Metrics, PoolAdapter> configurationProperties) {
        this.configurationProperties = configurationProperties;
        this.connectionFactory = configurationProperties.getPoolAdapter();
    }

    /**
     * @return configurationProperties
     */
    public ConfigurationProperties getConfigurationProperties() {
        return configurationProperties;
    }

    /**
     * @return connectionFactory
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}

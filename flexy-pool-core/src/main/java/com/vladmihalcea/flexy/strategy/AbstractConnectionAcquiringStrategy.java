package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.connection.ConnectionFactory;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * <code>AbstractConnectionAcquiringStrategy</code> implements the {@link ConnectionAcquiringStrategy} adding
 * the configuration and connectionFactory properties.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final ConfigurationProperties configurationProperties;
    private final ConnectionFactory connectionFactory;

    /**
     * Creates a strategy using the given {@link com.vladmihalcea.flexy.util.ConfigurationProperties}
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

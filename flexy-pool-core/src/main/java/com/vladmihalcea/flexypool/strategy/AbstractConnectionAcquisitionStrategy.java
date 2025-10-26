package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionFactory;
import com.vladmihalcea.flexypool.metric.Metrics;

import javax.sql.DataSource;

/**
 * <code>AbstractConnectionAcquisitionStrategy</code> implements the {@link ConnectionAcquisitionStrategy} adding
 * the configuration and connectionFactory properties.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public abstract class AbstractConnectionAcquisitionStrategy implements ConnectionAcquisitionStrategy {

    private final ConfigurationProperties configurationProperties;

    private final ConnectionFactory connectionFactory;

    /**
     * Creates a strategy using the given {@link ConfigurationProperties}
     *
     * @param configurationProperties configurationProperties
     */
    protected AbstractConnectionAcquisitionStrategy(
            ConfigurationProperties<? extends DataSource, Metrics, PoolAdapter> configurationProperties) {
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

package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

import javax.sql.DataSource;

/**
 * <code>PoolAdapterFactory</code> - The Pool Adapter Factory is used for creating new Pool Adaptor instances
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public interface PoolAdapterFactory<T extends DataSource> {

    /**
     * Creates a new pool adapter instance for the given configuration.
     *
     * @param configurationProperties configuration
     * @return pool adapter instance
     */
    PoolAdapter<T> newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

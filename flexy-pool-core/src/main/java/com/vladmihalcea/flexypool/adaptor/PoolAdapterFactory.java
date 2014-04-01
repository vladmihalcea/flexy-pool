package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * PoolAdapterFactory - Pool Adapter Configuration based factory
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapterFactory<T extends DataSource> {

    /**
     * Creates a new pool adapter instance for the given configuration.
     * @param configurationProperties configuration
     * @return pool adapter instance
     */
    PoolAdapter<T> newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

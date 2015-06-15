package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * <code>ConnectionAcquiringStrategyFactory</code> - ConnectionAcquiringStrategy Configuration based factory
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquiringStrategyFactory<S extends ConnectionAcquiringStrategy, T extends DataSource> {

    /**
     * Creates a new strategy instance for the given configuration.
     * @param configurationProperties configuration
     * @return strategy instance
     */
    S newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

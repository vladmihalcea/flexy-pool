package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

import javax.sql.DataSource;

/**
 * <code>ConnectionAcquisitionStrategyFactory</code> - ConnectionAcquisitionStrategy Configuration based factory
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquisitionStrategyFactory<S extends ConnectionAcquisitionStrategy, T extends DataSource> {

    /**
     * Creates a new strategy instance for the given configuration.
     *
     * @param configurationProperties configuration
     * @return strategy instance
     */
    S newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

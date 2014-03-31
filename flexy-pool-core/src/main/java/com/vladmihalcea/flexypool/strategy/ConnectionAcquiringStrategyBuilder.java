package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * ConnectionAcquiringStrategyBuilder - ConnectionAcquiringStrategy Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquiringStrategyBuilder<S extends ConnectionAcquiringStrategy, T extends DataSource> {

    S build(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

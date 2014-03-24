package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * Factory - ConnectionAcquiringStrategy Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquiringStrategyBuilder<S extends ConnectionAcquiringStrategy, T extends DataSource> {

    S build(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

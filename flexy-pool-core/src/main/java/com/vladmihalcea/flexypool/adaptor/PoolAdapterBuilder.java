package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * PoolAdapterBuilder - Pool Adapter Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapterBuilder<T extends DataSource> {

    PoolAdapter<T> build(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

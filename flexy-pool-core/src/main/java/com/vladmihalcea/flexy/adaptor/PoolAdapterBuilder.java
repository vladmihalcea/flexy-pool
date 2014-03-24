package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * PoolAdapterBuilder - Pool Adapter Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapterBuilder<T extends DataSource> {

    PoolAdapter<T> build(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties);
}

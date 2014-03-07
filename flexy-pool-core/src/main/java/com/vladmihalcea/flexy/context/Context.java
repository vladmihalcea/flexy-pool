package com.vladmihalcea.flexy.context;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.Metrics;

import javax.sql.DataSource;

/**
 * Context - Context
 *
 * @author Vlad Mihalcea
 */
public class Context {

    private final Configuration configuration;
    private final Metrics metrics;
    private final PoolAdapter poolAdapter;

    public Context(
            Configuration configuration,
            Metrics metrics,
            PoolAdapter poolAdapter) {
        this.configuration = configuration;
        this.metrics = metrics;
        this.poolAdapter = poolAdapter;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public PoolAdapter getPoolAdapter() {
        return poolAdapter;
    }
}

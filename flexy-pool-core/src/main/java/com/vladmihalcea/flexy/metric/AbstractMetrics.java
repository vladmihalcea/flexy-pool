package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.config.Configuration;

/**
 * AbstractMetrics - Base Metrics
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractMetrics implements Metrics {

    private final Configuration configuration;

    protected AbstractMetrics(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}

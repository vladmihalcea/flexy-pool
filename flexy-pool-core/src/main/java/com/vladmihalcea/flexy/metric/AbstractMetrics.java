package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.config.FlexyConfiguration;

/**
 * AbstractMetrics - Base Metrics
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractMetrics implements Metrics {

    private final FlexyConfiguration configuration;

    protected AbstractMetrics(FlexyConfiguration configuration) {
        this.configuration = configuration;
    }

    public FlexyConfiguration getConfiguration() {
        return configuration;
    }
}

package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.config.Configuration;

/**
 * <code>AbstractMetrics</code> implements the {@link com.vladmihalcea.flexy.metric.Metrics} interface and
 * adds the Configuration property.
 *
 * @author	Vlad Mihalcea
 * @version	%I%, %E%
 * @since	1.0
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

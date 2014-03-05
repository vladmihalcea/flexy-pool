package com.vladmihalcea.flexy.context;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.lifecycle.LifeCycleAware;
import com.vladmihalcea.flexy.metric.codahale.CodahaleMetrics;
import com.vladmihalcea.flexy.metric.Metrics;

/**
 * Context - Context
 *
 * @author Vlad Mihalcea
 */
public class Context implements LifeCycleAware {

    private final Configuration configuration;
    private final Metrics metrics;

    public Context(Configuration configuration, Metrics metrics) {
        this.configuration = configuration;
        this.metrics = metrics;
    }

    public Context(Configuration configuration) {
        this.configuration = configuration;
        this.metrics = new CodahaleMetrics(configuration, Metrics.class);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public void start() {
        metrics.start();
    }

    @Override
    public void stop() {
        metrics.stop();
    }
}

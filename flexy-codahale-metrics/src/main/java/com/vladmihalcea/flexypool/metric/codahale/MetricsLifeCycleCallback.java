package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

/**
 * <code>MetricsLifeCycleCallback</code> allows you to add lifecycle event handlers
 * to Codahale Metrics.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public interface MetricsLifeCycleCallback extends LifeCycleCallback {

    /**
     * Init callback for the given metric registry.
     * @param configurationProperties configuration properties
     * @param metricRegistry metric registry
     */
    MetricsLifeCycleCallback init(ConfigurationProperties configurationProperties, MetricRegistry metricRegistry);
}

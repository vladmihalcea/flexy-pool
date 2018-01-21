package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback;

/**
 * <code>MetricsLifeCycleCallback</code> allows you to add lifecycle event handlers
 * to Codahale Metrics.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public interface MetricsLifeCycleCallback extends LifeCycleCallback {

    /**
     * Init callback for the given metric registry.
     *
     * @param configurationProperties configuration properties
     * @param metricRegistry metric registry
     *
     * @return MetricsLifeCycleCallback
     */
    MetricsLifeCycleCallback init(ConfigurationProperties configurationProperties, MetricRegistry metricRegistry);
}

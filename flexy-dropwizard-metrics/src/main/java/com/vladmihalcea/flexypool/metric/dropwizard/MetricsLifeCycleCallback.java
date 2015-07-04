package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

/**
 * <code>MetricsLifeCycleCallback</code> allows you to add lifecycle event handlers
 * to Dropwizard Metrics.
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public interface MetricsLifeCycleCallback extends LifeCycleCallback {

    /**
     * Init callback for the given metric registry.
     * @param configurationProperties configuration properties
     * @param metricRegistry metric registry
     */
    MetricsLifeCycleCallback init(ConfigurationProperties configurationProperties, MetricRegistry metricRegistry);
}

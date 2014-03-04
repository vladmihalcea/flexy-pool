package com.vladmihalcea.flexy.metric;

/**
 * MetricsAware - Interface to obtaining the current associated Metrics
 *
 * @author Vlad Mihalcea
 */
public interface MetricsAware {

    /**
     * Initialize and bind a metrics to the current object.
     */
    void initializeMetrics();

    /**
     * Get the current object metrics.
     *
     * @return current object metrics.
     */
    Metrics getMetrics();
}

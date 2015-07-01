package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.Metric;
import io.dropwizard.metrics.Reservoir;

/**
 * <code>ReservoirFactory</code> defines how a Codahale reservoir is allocated to a specific Metric.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public interface ReservoirFactory {

    /**
     * Create new reservoir for the given metric name.
     * @param metricClass metrics class
     * @param metricName metrics name
     * @return metrics specific reservoir
     */
    Reservoir newInstance(Class<? extends Metric> metricClass, String metricName);
}

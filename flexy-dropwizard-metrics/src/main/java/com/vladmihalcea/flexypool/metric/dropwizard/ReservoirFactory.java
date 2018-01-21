package com.vladmihalcea.flexypool.metric.dropwizard;

import com.codahale.metrics.Metric;
import com.codahale.metrics.Reservoir;

/**
 * <code>ReservoirFactory</code> defines how a Dropwizard reservoir is allocated to a specific Metric.
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
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

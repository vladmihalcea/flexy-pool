package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.Reservoir;

/**
 * <code>ReservoirFactory</code> defines how a Codahale reservoir is allocated to a specific Metric.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public interface ReservoirFactory {

    Reservoir newInstance(String metricName);
}

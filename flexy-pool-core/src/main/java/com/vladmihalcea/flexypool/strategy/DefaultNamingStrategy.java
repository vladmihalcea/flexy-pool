package com.vladmihalcea.flexypool.strategy;

/**
 * <code>DefaultNamingStrategy</code> defines the default {@link MetricNamingStrategy}
 * implementation.
 *
 * @author Atle Tokle
 * @since 2.2.2
 */
public class DefaultNamingStrategy implements MetricNamingStrategy {

    @Override
    public String getMetricName(String name) {
        return name;
    }

    @Override
    public boolean useUniquePoolName() {
        return false;
    }
}

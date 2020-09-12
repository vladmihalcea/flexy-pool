package com.vladmihalcea.flexypool.strategy;

/**
 * <code>DefaultNamingStrategy</code> implements the {@link MetricNamingStrategy}
 * keep namings as before
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
    public boolean usePoolUniqueName() {
        return false;
    }
}

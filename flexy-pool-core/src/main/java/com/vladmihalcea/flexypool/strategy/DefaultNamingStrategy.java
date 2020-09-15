package com.vladmihalcea.flexypool.strategy;

/**
 * <code>DefaultNamingStrategy</code> defines the default {@link MetricNamingStrategy}
 * implementation.
 *
 * @author Atle Tokle
 * @since 2.2.2
 */
public class DefaultNamingStrategy implements MetricNamingStrategy {

    public static final DefaultNamingStrategy INSTANCE = new DefaultNamingStrategy();
}

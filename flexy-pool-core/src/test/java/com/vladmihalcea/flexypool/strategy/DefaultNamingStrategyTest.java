package com.vladmihalcea.flexypool.strategy;

import junit.framework.TestCase;

public class DefaultNamingStrategyTest extends TestCase {
    private MetricNamingStrategy namingStrategy = new DefaultNamingStrategy();

    public void testNameStrategy() {
        assertEquals("metricName", namingStrategy.getMetricName("metricName"));
    }

    public void testUseUniqueNames() {
        assertFalse(namingStrategy.usePoolUniqueName());
    }

}
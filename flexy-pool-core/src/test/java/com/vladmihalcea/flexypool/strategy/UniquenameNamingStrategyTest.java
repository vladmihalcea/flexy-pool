package com.vladmihalcea.flexypool.strategy;

import junit.framework.TestCase;

public class UniquenameNamingStrategyTest extends TestCase {
    private MetricNamingStrategy namingStrategy = new UniquenameNamingStrategy();

    public void testNameStrategy() {
        assertEquals("flexypool_metricName", namingStrategy.getMetricName("metricName"));
    }

    public void testUseUniqueNames() {
        assertTrue(namingStrategy.usePoolUniqueName());
    }
}
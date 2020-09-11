package com.vladmihalcea.flexypool.strategy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UniqueNamingStrategyTest {

    private MetricNamingStrategy namingStrategy = new UniqueNamingStrategy();

    @Test
    public void testNameStrategy() {
        assertEquals("flexypool_metricName", namingStrategy.getMetricName("metricName"));
    }

    @Test
    public void testUseUniqueNames() {
        assertTrue(namingStrategy.useUniquePoolName());
    }
}
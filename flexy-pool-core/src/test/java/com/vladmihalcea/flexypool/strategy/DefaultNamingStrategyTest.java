package com.vladmihalcea.flexypool.strategy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DefaultNamingStrategyTest {

    private MetricNamingStrategy namingStrategy = new DefaultNamingStrategy();

    @Test
    public void testNameStrategy() {
        assertEquals("metricName", namingStrategy.getMetricName("metricName"));
    }

    @Test
    public void testUseUniqueNames() {
        assertFalse(namingStrategy.useUniquePoolName());
    }

}
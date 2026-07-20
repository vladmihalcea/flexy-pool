package com.vladmihalcea.flexypool.strategy;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


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
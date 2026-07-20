package com.vladmihalcea.flexypool.strategy;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueNamingStrategyTest {

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
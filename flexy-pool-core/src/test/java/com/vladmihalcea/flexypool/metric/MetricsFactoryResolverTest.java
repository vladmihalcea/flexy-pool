package com.vladmihalcea.flexypool.metric;



import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * MetricsFactoryResolverTest - MetricsFactoryResolver Test
 *
 * @author Vlad Mihalcea
 */
public class MetricsFactoryResolverTest {

    @Test
    public void testResolve() {
        MetricsFactoryResolver resolver = MetricsFactoryResolver.INSTANCE;
        MetricsFactory metricsFactory = resolver.resolve();
        assertNotNull(metricsFactory);
    }
}
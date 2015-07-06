package com.vladmihalcea.flexypool.metric;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
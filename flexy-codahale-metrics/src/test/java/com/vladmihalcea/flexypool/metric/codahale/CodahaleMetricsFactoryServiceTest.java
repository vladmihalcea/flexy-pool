package com.vladmihalcea.flexypool.metric.codahale;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * CodahaleMetricsFactoryServiceTest - CodahaleMetricsFactoryService Test
 *
 * @author Vlad Mihalcea
 */
public class CodahaleMetricsFactoryServiceTest {

    @Test
    public void testLoadSuccess() {
        MetricsFactory metricsFactory = new CodahaleMetricsFactoryService().load();
        assertNotNull(metricsFactory);
        assertSame(CodahaleMetrics.FACTORY, metricsFactory);
    }

    @Test
    public void testLoadFailure() {
        ClassLoader currentClassLoader = ClassLoaderUtils.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(new ClassLoader() {
                @Override
                protected Class loadClass(String class_name, boolean resolve) throws ClassNotFoundException {
                    if(class_name.equals("com.codahale.metrics.Metric")) {
                        return null;
                    }
                    return super.loadClass(class_name, resolve);
                }
            });
            MetricsFactory metricsFactory = new CodahaleMetricsFactoryService().load();
            assertNull(metricsFactory);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}
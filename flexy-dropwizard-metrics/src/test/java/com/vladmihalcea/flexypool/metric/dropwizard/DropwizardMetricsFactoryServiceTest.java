package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * DropwizardMetricsFactoryServiceTest - DropwizardMetricsFactoryService Test
 *
 * @author Vlad Mihalcea
 */
public class DropwizardMetricsFactoryServiceTest {

    @Test
    public void testLoadSuccess() {
        MetricsFactory metricsFactory = new DropwizardMetricsFactoryService().load();
        assertNotNull(metricsFactory);
        assertSame(DropwizardMetrics.FACTORY, metricsFactory);
    }

    @Test
    public void testLoadFailure() {
        ClassLoader currentClassLoader = ClassLoaderUtils.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(new ClassLoader() {
                @Override
                protected Class loadClass(String class_name, boolean resolve) throws ClassNotFoundException {
                    if(class_name.equals("io.dropwizard.metrics.Metric")) {
                        return null;
                    }
                    return super.loadClass(class_name, resolve);
                }
            });
            MetricsFactory metricsFactory = new DropwizardMetricsFactoryService().load();
            assertNull(metricsFactory);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}
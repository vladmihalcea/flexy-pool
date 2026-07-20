package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import org.junit.jupiter.api.Test;


import static com.vladmihalcea.flexypool.metric.dropwizard.DropwizardMetricsFactoryService.METRICS_CLASS_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * DropwizardMetricsFactoryServiceTest - DropwizardMetricsFactoryService Test
 *
 * @author Vlad Mihalcea
 */
class DropwizardMetricsFactoryServiceTest {

    @Test
    void testLoadSuccess() {
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
                    if(class_name.equals(METRICS_CLASS_NAME)) {
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
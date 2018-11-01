package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import org.junit.Test;

import static com.vladmihalcea.flexypool.metric.micrometer.MicrometerMetricsFactoryService.METRICS_CLASS_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * MicrometerMetricsFactoryServiceTest - MicrometerMetricsFactoryService Test
 *
 * @author Vlad Mihalcea
 */
public class MicrometerMetricsFactoryServiceTest {

    @Test
    public void testLoadSuccess() {
        MetricsFactory metricsFactory = new MicrometerMetricsFactoryService().load();
        assertNotNull(metricsFactory);
        assertSame(MicrometerMetrics.FACTORY, metricsFactory);
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
            MetricsFactory metricsFactory = new MicrometerMetricsFactoryService().load();
            assertNull(metricsFactory);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }
}

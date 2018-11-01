package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryService;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;

/**
 * <code>MicrometerMetricsFactoryService</code> - Dropwizard MetricsFactoryService
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class MicrometerMetricsFactoryService implements MetricsFactoryService {

    public static final String METRICS_CLASS_NAME = "io.micrometer.core.instrument.Metrics";

    /**
     * Load MicrometerMetrics Factory if the Dropwizard Metrics is available at runtime
     *
     * @return MicrometerMetrics Factory
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(METRICS_CLASS_NAME) ? MicrometerMetrics.FACTORY : null;
    }
}

package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryService;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;

/**
 * <code>MicrometerMetricsFactoryService</code> - Micrometer MetricsFactoryService
 *
 * @author Vlad Mihalcea
 * @since 2.1.0
 */
public class MicrometerMetricsFactoryService implements MetricsFactoryService {

    public static final String METRICS_CLASS_NAME = "io.micrometer.core.instrument.Metrics";

    /**
     * Load {@link MicrometerMetrics} if the Micrometer Metrics is available at runtime.
     *
     * @return Micrometer {@link MetricsFactory}
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(METRICS_CLASS_NAME) ? MicrometerMetrics.FACTORY : null;
    }
}

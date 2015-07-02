package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryService;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;

/**
 * <code>DropwizardMetricsFactoryService</code> - Dropwizard MetricsFactoryService
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class DropwizardMetricsFactoryService implements MetricsFactoryService {

    public static final String METRICS_CLASS_NAME = "io.dropwizard.metrics.Metric";

    /**
     * Load DropwizardMetrics Factory if the Dropwizard Metrics is available at runtime
     *
     * @return DropwizardMetrics Factory
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(METRICS_CLASS_NAME) ? DropwizardMetrics.FACTORY : null;
    }
}

package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.Metric;
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

    /**
     * Load DropwizardMetrics Factory if the Dropwizard Metrics is available at runtime
     *
     * @return DropwizardMetrics Factory
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(Metric.class.getName()) ? DropwizardMetrics.FACTORY : null;
    }
}

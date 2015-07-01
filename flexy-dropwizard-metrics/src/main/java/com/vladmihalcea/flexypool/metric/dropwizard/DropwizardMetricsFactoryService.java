package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.Metric;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryService;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;

/**
 * DropwizardMetricsFactoryService - Dropwizard MetricsFactoryService
 *
 * @author Vlad Mihalcea
 */
public class DropwizardMetricsFactoryService implements MetricsFactoryService {

    /**
     * Load DropwizardMetrics Factory if the Codahale Metrics is available at runtime
     *
     * @return DropwizardMetrics Factory
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(Metric.class.getName()) ? DropwizardMetrics.FACTORY : null;
    }
}

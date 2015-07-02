package com.vladmihalcea.flexypool.metric.codahale;

import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryService;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;

/**
 * <code>CodahaleMetricsFactoryService</code> - Codahale MetricsFactoryService
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class CodahaleMetricsFactoryService implements MetricsFactoryService {

    public static final String METRICS_CLASS_NAME = "com.codahale.metrics.Metric";

    /**
     * Load CodahaleMetrics Factory if the Codahale Metrics is available at runtime
     *
     * @return CodahaleMetrics Factory
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(METRICS_CLASS_NAME) ? CodahaleMetrics.FACTORY : null;
    }
}

package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.Metric;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryService;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;

/**
 * CodahaleMetricsFactoryService - Codahale MetricsFactoryService
 *
 * @author Vlad Mihalcea
 */
public class CodahaleMetricsFactoryService implements MetricsFactoryService {

    /**
     * Load CodahaleMetrics Factory if the Codahale Metrics is available at runtime
     *
     * @return CodahaleMetrics Factory
     */
    @Override
    public MetricsFactory load() {
        return ClassLoaderUtils.findClass(Metric.class.getName()) ? CodahaleMetrics.FACTORY : null;
    }
}

package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

/**
 * JmxMetricReporter - Jmx Metric Reporter
 *
 * @author Vlad Mihalcea
 */
public class JmxMetricReporter implements MetricsLifeCycleCallback {

    private JmxReporter jmxReporter;

    @Override
    public JmxMetricReporter init(ConfigurationProperties configurationProperties, MetricRegistry metricRegistry) {
        if (configurationProperties.isJmxEnabled()) {
            jmxReporter = JmxReporter
                    .forRegistry(metricRegistry)
                    .inDomain(MetricRegistry.name(getClass(), configurationProperties.getUniqueName()))
                    .build();
        }
        return this;
    }

    @Override
    public void start() {
        if (jmxReporter != null) {
            jmxReporter.start();
        }
    }

    @Override
    public void stop() {
        if (jmxReporter != null) {
            jmxReporter.stop();
        }
    }
}

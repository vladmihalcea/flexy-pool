package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.JmxReporter;
import io.dropwizard.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

/**
 * <code>JmxMetricReporter</code> - Jmx Metric Reporter
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class JmxMetricReporter implements MetricsLifeCycleCallback {

    private JmxReporter jmxReporter;

    /**
     * The JMX Reporter is activated only if the jmxEnabled property is set. If the jmxAutoStart property is enabled,
     * the JMX Reporter will start automatically.
     *
     * @param configurationProperties configuration properties
     * @param metricRegistry metric registry
     * @return {@link JmxMetricReporter}
     */
    @Override
    public JmxMetricReporter init(ConfigurationProperties configurationProperties, MetricRegistry metricRegistry) {
        if (configurationProperties.isJmxEnabled()) {
            jmxReporter = JmxReporter
                    .forRegistry(metricRegistry)
                    .inDomain(getClass().getName() + "." + configurationProperties.getUniqueName())
                    .build();
        }
        if(configurationProperties.isJmxAutoStart()) {
            start();
        }
        return this;
    }

    /**
     * Start JMX Reporter
     */
    @Override
    public void start() {
        if (jmxReporter != null) {
            jmxReporter.start();
        }
    }

    /**
     * Stop JMX Reporter
     */
    @Override
    public void stop() {
        if (jmxReporter != null) {
            jmxReporter.stop();
        }
    }
}

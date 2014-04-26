package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Slf4jMetricReporter - Slf4j Metric Reporter
 *
 * @author Vlad Mihalcea
 */
public class Slf4jMetricReporter implements MetricsLifeCycleCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodahaleMetrics.class);

    private Slf4jReporter slf4jReporter;

    private long metricLogReporterMillis;

    @Override
    public Slf4jMetricReporter init(ConfigurationProperties configurationProperties, MetricRegistry metricRegistry) {
        metricLogReporterMillis = configurationProperties.getMetricLogReporterMillis();
        if (metricLogReporterMillis > 0) {
            this.slf4jReporter = Slf4jReporter
                    .forRegistry(metricRegistry)
                    .outputTo(LOGGER)
                    .build();
        }
        return this;
    }

    @Override
    public void start() {
        if(slf4jReporter != null) {
            slf4jReporter.start(metricLogReporterMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        if(slf4jReporter != null) {
            slf4jReporter.stop();
        }
    }
}

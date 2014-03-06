package com.vladmihalcea.flexy.metric.codahale;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.AbstractMetrics;
import com.vladmihalcea.flexy.metric.Histogram;
import com.vladmihalcea.flexy.metric.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * CodahaleMetrics - com.codahale.metrics based Metrics implementation.
 * <p/>
 * It may report the cumulated metrics to both the current LOGGER or platform JMX server.
 *
 * @author Vlad Mihalcea
 */
public class CodahaleMetrics extends AbstractMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodahaleMetrics.class);

    private final MetricRegistry metricRegistry;
    private final Slf4jReporter logReporter;
    private final JmxReporter jmxReporter;

    public CodahaleMetrics(Configuration configuration, Class<?> clazz) {
        super(configuration);
        this.metricRegistry = new MetricRegistry();
        this.logReporter = Slf4jReporter
                .forRegistry(metricRegistry)
                .outputTo(LOGGER)
                .build();
        if (configuration.isJmxEnabled()) {
            jmxReporter = JmxReporter
                    .forRegistry(metricRegistry)
                    .inDomain(MetricRegistry.name(clazz, configuration.getUniqueName()))
                    .build();
        } else {
            jmxReporter = null;
        }
    }

    @Override
    public Histogram histogram(String name) {
        return new CodahaleHistogram(metricRegistry.histogram(name));
    }

    @Override
    public Timer timer(String name) {
        return new CodahaleTimer(metricRegistry.timer(name));
    }

    public void start() {
        logReporter.start(getConfiguration().getMetricLogReporterPeriod(), TimeUnit.MINUTES);
        if (jmxReporter != null) {
            jmxReporter.start();
        }
    }

    public void stop() {
        logReporter.stop();
        if (jmxReporter != null) {
            jmxReporter.stop();
        }
    }
}

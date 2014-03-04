package com.vladmihalcea.flexy.metric;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.vladmihalcea.flexy.config.FlexyConfiguration;
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

    private final static Logger LOGGER = LoggerFactory.getLogger(CodahaleMetrics.class);

    private final MetricRegistry metricRegistry;
    private final Slf4jReporter logReporter;
    private final JmxReporter jmxReporter;

    public CodahaleMetrics(FlexyConfiguration configuration, Class<?> clazz) {
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

    public void updateHistogram(String name, long value) {
        metricRegistry.histogram(name).update(value);
    }

    public void updateTimer(String name, long value, TimeUnit timeUnit) {
        metricRegistry.timer(name).update(value, timeUnit);
    }

    public void start() {
        logReporter.start(5, TimeUnit.MINUTES);
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

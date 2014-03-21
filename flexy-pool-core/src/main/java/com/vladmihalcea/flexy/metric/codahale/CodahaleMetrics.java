package com.vladmihalcea.flexy.metric.codahale;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.builder.MetricsBuilder;
import com.vladmihalcea.flexy.metric.AbstractMetrics;
import com.vladmihalcea.flexy.metric.Histogram;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.metric.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <code>CodahaleMetrics</code> extends the {@link AbstractMetrics} class and configures the Codahale
 * {@link MetricRegistry}. By default, the {@link Slf4jReporter} is used for logging metrics data.
 * If the Jmx is enabled, a {@link JmxReporter} will also be added.
 *
 * This class implements the {@link com.vladmihalcea.flexy.lifecycle.LifeCycleAware} interface so it can
 * start/stop the metrics reports.
 *
 * @author	Vlad Mihalcea
 * @version	%I%, %E%
 * @since	1.0
 */
public class CodahaleMetrics extends AbstractMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodahaleMetrics.class);

    public static final MetricsBuilder BUILDER = new MetricsBuilder() {
        @Override
        public Metrics build(Configuration configuration) {
            return new CodahaleMetrics(configuration);
        }
    };

    private final MetricRegistry metricRegistry;
    private final Slf4jReporter logReporter;
    private final JmxReporter jmxReporter;

    public CodahaleMetrics(Configuration configuration) {
        super(configuration);
        this.metricRegistry = new MetricRegistry();
        this.logReporter = Slf4jReporter
                .forRegistry(metricRegistry)
                .outputTo(LOGGER)
                .build();
        if (configuration.isJmxEnabled()) {
            jmxReporter = JmxReporter
                    .forRegistry(metricRegistry)
                    .inDomain(MetricRegistry.name(getClass(), configuration.getUniqueName()))
                    .build();
        } else {
            jmxReporter = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Histogram histogram(String name) {
        return new CodahaleHistogram(metricRegistry.histogram(name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timer timer(String name) {
        return new CodahaleTimer(metricRegistry.timer(name));
    }

    /**
     * Start metrics reports.
     */
    public void start() {
        logReporter.start(getConfiguration().getMetricLogReporterPeriod(), TimeUnit.MINUTES);
        if (jmxReporter != null) {
            jmxReporter.start();
        }
    }

    /**
     * Stop metrics reports.
     */
    public void stop() {
        logReporter.stop();
        if (jmxReporter != null) {
            jmxReporter.stop();
        }
    }
}

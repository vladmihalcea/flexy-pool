package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.vladmihalcea.flexypool.metric.*;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <code>CodahaleMetrics</code> extends the {@link AbstractMetrics} class and configures the Codahale
 * {@link MetricRegistry}. By default, the {@link Slf4jReporter} is used for logging metrics data.
 * If the Jmx is enabled, a {@link JmxReporter} will also be added.
 * <p/>
 * This class implements the {@link com.vladmihalcea.flexypool.lifecycle.LifeCycleAware} interface so it can
 * start/stop the metrics reports.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class CodahaleMetrics extends AbstractMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodahaleMetrics.class);

    public static final MetricsBuilder BUILDER = new MetricsBuilder() {
        @Override
        public Metrics build(ConfigurationProperties configurationProperties) {
            return new CodahaleMetrics(configurationProperties);
        }
    };

    private final MetricRegistry metricRegistry;
    private final Slf4jReporter logReporter;
    private final JmxReporter jmxReporter;

    public CodahaleMetrics(ConfigurationProperties configurationProperties) {
        super(configurationProperties);
        this.metricRegistry = new MetricRegistry();
        this.logReporter = Slf4jReporter
                .forRegistry(metricRegistry)
                .outputTo(LOGGER)
                .build();
        if (configurationProperties.isJmxEnabled()) {
            jmxReporter = JmxReporter
                    .forRegistry(metricRegistry)
                    .inDomain(MetricRegistry.name(getClass(), configurationProperties.getUniqueName()))
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
        logReporter.start(getConfigurationProperties().getMetricLogReporterPeriod(), TimeUnit.MINUTES);
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

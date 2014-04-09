package com.vladmihalcea.flexypool.metric.codahale;

import com.codahale.metrics.*;
import com.vladmihalcea.flexypool.metric.*;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Timer;
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

    public static class ReservoirMetricsFactory implements MetricsFactory {

        private final ReservoirFactory reservoirFactory;

        public ReservoirMetricsFactory(ReservoirFactory reservoirFactory) {
            this.reservoirFactory = reservoirFactory;
        }

        @Override
        public Metrics newInstance(ConfigurationProperties configurationProperties) {
            return new CodahaleMetrics(configurationProperties, reservoirFactory);
        }
    }

    public static final MetricsFactory FACTORY = new ReservoirMetricsFactory(new ReservoirFactory() {
        @Override
        public Reservoir newInstance(String metricName) {
            return new ExponentiallyDecayingReservoir();
        }
    });

    public static final MetricsFactory UNIFORM_RESERVOIR_FACTORY = new ReservoirMetricsFactory(new ReservoirFactory() {
        @Override
        public Reservoir newInstance(String metricName) {
            return new UniformReservoir();
        }
    });

    private final MetricRegistry metricRegistry;
    private final Slf4jReporter logReporter;
    private final JmxReporter jmxReporter;
    private final ReservoirFactory reservoirFactory;

    public CodahaleMetrics(ConfigurationProperties configurationProperties, ReservoirFactory reservoirFactory) {
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
        this.reservoirFactory = reservoirFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Histogram histogram(String name) {
        com.codahale.metrics.Histogram histogram = new com.codahale.metrics.Histogram(
                reservoirFactory.newInstance(name)
        );
        return new CodahaleHistogram(metricRegistry.register(name, histogram));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timer timer(String name) {
        com.codahale.metrics.Timer timer = new com.codahale.metrics.Timer(
                reservoirFactory.newInstance(name)
        );
        return new CodahaleTimer(metricRegistry.register(name, timer));
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

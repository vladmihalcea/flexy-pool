package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.*;
import com.vladmihalcea.flexypool.metric.*;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * <code>DropwizardMetrics</code> extends the {@link AbstractMetrics} class and configures the Dropwizard
 * {@link MetricRegistry}. By default, the {@link Slf4jReporter} is used for logging metrics data.
 * If the Jmx is enabled, a {@link JmxReporter} will also be added.
 * <p/>
 * This class implements the {@link com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback} interface so it can
 * start/stop the metrics reports.
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class DropwizardMetrics extends AbstractMetrics {

    /**
     * The ReservoirMetricsFactory takes a {@link ReservoirFactory} and supplies it to the DropwizardMetrics instance.
     */
    public static class ReservoirMetricsFactory implements MetricsFactory {

        private final ReservoirFactory reservoirFactory;

        /**
         * Init constructor
         * @param reservoirFactory reservoir factory
         */
        public ReservoirMetricsFactory(ReservoirFactory reservoirFactory) {
            this.reservoirFactory = reservoirFactory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Metrics newInstance(ConfigurationProperties configurationProperties) {
            return new DropwizardMetrics(configurationProperties, reservoirFactory);
        }
    }

    /**
     * Singleton factory object reference
     */
    public static final MetricsFactory FACTORY = new ReservoirMetricsFactory(new ReservoirFactory() {
        @Override
        public Reservoir newInstance(Class<? extends Metric> metricClass, String metricName) {
            return new ExponentiallyDecayingReservoir();
        }
    });

    public static final MetricsFactory UNIFORM_RESERVOIR_FACTORY = new ReservoirMetricsFactory(new ReservoirFactory() {
        @Override
        public Reservoir newInstance(Class<? extends Metric> metricClass, String metricName) {
            return new UniformReservoir();
        }
    });

    private final MetricRegistry metricRegistry;

    private final ReservoirFactory reservoirFactory;

    private final Collection<MetricsLifeCycleCallback> callbacks = new LinkedHashSet<MetricsLifeCycleCallback>();

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     * @param reservoirFactory reservoir factory
     * @param callbacks life cycle callbacks
     */
    public DropwizardMetrics(ConfigurationProperties configurationProperties,
                             ReservoirFactory reservoirFactory,
                             MetricsLifeCycleCallback... callbacks) {
        super(configurationProperties);
        this.metricRegistry = new MetricRegistry();
        this.reservoirFactory = reservoirFactory;
        this.callbacks.add(new Slf4jMetricReporter().init(configurationProperties, metricRegistry));
        this.callbacks.add(new JmxMetricReporter().init(configurationProperties, metricRegistry));
        for (MetricsLifeCycleCallback callback : callbacks) {
            this.callbacks.add(callback.init(configurationProperties, metricRegistry));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Histogram histogram(String name) {
        io.dropwizard.metrics.Histogram histogram = new io.dropwizard.metrics.Histogram(
                reservoirFactory.newInstance(io.dropwizard.metrics.Histogram.class, name)
        );
        return new DropwizardHistogram(metricRegistry.register(name, histogram));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timer timer(String name) {
        io.dropwizard.metrics.Timer timer = new io.dropwizard.metrics.Timer(
                reservoirFactory.newInstance(io.dropwizard.metrics.Timer.class, name)
        );
        return new DropwizardTimer(metricRegistry.register(name, timer));
    }

    /**
     * Start metrics reports.
     */
    public void start() {
        for (MetricsLifeCycleCallback callback : callbacks) {
           callback.start();
        }
    }

    /**
     * Stop metrics reports.
     */
    public void stop() {
        for (MetricsLifeCycleCallback callback : callbacks) {
            callback.stop();
        }
    }
}

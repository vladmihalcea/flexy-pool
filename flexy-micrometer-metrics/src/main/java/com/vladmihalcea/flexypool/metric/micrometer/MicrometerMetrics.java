package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.metric.*;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Collections;
import java.util.List;

/**
 * <code>MicrometerMetrics</code> extends the {@link AbstractMetrics} class and configures the Micrometer {@link MeterRegistry}.
 *
 * @author Vlad Mihalcea
 * @since 2.1.0
 */
public class MicrometerMetrics extends AbstractMetrics {

    private static final String POOLNAME_TAG = "poolname";

    public static final MetricsFactory FACTORY = new MetricsFactory() {
        @Override public Metrics newInstance(ConfigurationProperties configurationProperties) {
            return new MicrometerMetrics(configurationProperties, io.micrometer.core.instrument.Metrics.globalRegistry);
        }
    };

    private final MeterRegistry metricRegistry;

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     * @param metricRegistry metric registry
     */
    public MicrometerMetrics(ConfigurationProperties configurationProperties,
                             MeterRegistry metricRegistry) {
        super(configurationProperties);
        this.metricRegistry = metricRegistry;
    }

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     */
    public MicrometerMetrics(ConfigurationProperties configurationProperties) {
        this(configurationProperties, new SimpleMeterRegistry());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Histogram histogram(String name) {
        return new MicrometerHistogram(
            metricRegistry.summary(
                getConfigurationProperties().getMetricNamingStrategy().getMetricName(name),
                poolNameTag()
            )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timer timer(String name) {
        return new MicrometerTimer(
            metricRegistry.timer(
                getConfigurationProperties().getMetricNamingStrategy().getMetricName(name),
                poolNameTag()
            )
        );
    }

    private List<Tag> poolNameTag() {
        return getConfigurationProperties().getMetricNamingStrategy().useUniquePoolName()
                ? Collections.singletonList(new ImmutableTag(POOLNAME_TAG, getConfigurationProperties().getUniqueName()))
                : Collections.emptyList();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}

package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * MicrometerMetricsTest - MicrometerMetrics Test
 *
 * @author Vlad Mihalcea
 */
public class MicrometerMetricsTest {

    @Mock
    private ConfigurationProperties configurationProperties;

    @Mock
    private MeterRegistry metricRegistry;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMetricRegistry() {
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties, metricRegistry);
        assertSame(metricRegistry, ReflectionUtils.getFieldValue(micrometerMetrics, "metricRegistry"));
    }

    @Test
    public void testHistogram() {
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties);
        assertSame(configurationProperties, micrometerMetrics.getConfigurationProperties());
        Histogram histogram = micrometerMetrics.histogram("histo");
        assertNotNull(histogram);
    }

    @Test
    public void testTimer() {
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties);
        Timer timer = micrometerMetrics.timer("timer");
        assertNotNull(timer);
    }

    @Test
    public void testStartStopUsingDefaultConfiguration() {
        when(configurationProperties.isJmxEnabled()).thenReturn(true);
        when(configurationProperties.getMetricLogReporterMillis()).thenReturn(5L);
        testStartStop(configurationProperties);
    }

    @Test
    public void testStartStopUsingNoJmx() {
        when(configurationProperties.isJmxEnabled()).thenReturn(false);
        when(configurationProperties.getMetricLogReporterMillis()).thenReturn(5L);
        testStartStop(configurationProperties);
    }

    public void testStartStop(ConfigurationProperties currentConfiguration) {
        Metrics codahaleMetrics = MicrometerMetrics.FACTORY.newInstance(currentConfiguration);
        codahaleMetrics.histogram("histo");
        codahaleMetrics.timer("timer");
        codahaleMetrics.start();
        codahaleMetrics.stop();
    }
}

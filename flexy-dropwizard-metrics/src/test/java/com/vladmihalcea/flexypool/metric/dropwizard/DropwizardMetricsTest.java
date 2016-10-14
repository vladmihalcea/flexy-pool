package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import io.dropwizard.metrics.MetricRegistry;
import io.dropwizard.metrics.Reservoir;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * DropwizardMetricsTest - DropwizardMetrics Test
 *
 * @author Vlad Mihalcea
 */
public class DropwizardMetricsTest {

    @Mock
    private ConfigurationProperties configurationProperties;

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private ReservoirFactory reservoirFactory;

    @Mock
    private Reservoir reservoir;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMetricRegistry() {
        DropwizardMetrics dropwizardMetrics = new DropwizardMetrics(configurationProperties, metricRegistry, reservoirFactory);
        assertSame(metricRegistry, ReflectionUtils.getFieldValue(dropwizardMetrics, "metricRegistry"));
    }

    @Test
    public void testHistogram() {
        DropwizardMetrics dropwizardMetrics = new DropwizardMetrics(configurationProperties, reservoirFactory);
        assertSame(configurationProperties, dropwizardMetrics.getConfigurationProperties());
        when(reservoirFactory.newInstance(io.dropwizard.metrics.Histogram.class, "histo")).thenReturn(reservoir);
        Histogram histogram = dropwizardMetrics.histogram("histo");
        verify(reservoirFactory, times(1)).newInstance(io.dropwizard.metrics.Histogram.class, "histo");
        assertNotNull(histogram);
    }

    @Test
    public void testTimer() {
        DropwizardMetrics dropwizardMetrics = new DropwizardMetrics(configurationProperties, reservoirFactory);
        when(reservoirFactory.newInstance(io.dropwizard.metrics.Timer.class, "timer")).thenReturn(reservoir);
        Timer timer = dropwizardMetrics.timer("timer");
        verify(reservoirFactory, times(1)).newInstance(io.dropwizard.metrics.Timer.class, "timer");
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
        Metrics codahaleMetrics = DropwizardMetrics.FACTORY.newInstance(currentConfiguration);
        codahaleMetrics.histogram("histo");
        codahaleMetrics.timer("timer");
        codahaleMetrics.start();
        codahaleMetrics.stop();
    }
}

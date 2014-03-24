package com.vladmihalcea.flexypool.metric.codahale;

import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * CodahaleMetricsTest - CodahaleMetrics Test
 *
 * @author Vlad Mihalcea
 */
public class CodahaleMetricsTest {

    @Mock
    private ConfigurationProperties configurationProperties;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHistogram() {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(configurationProperties);
        Histogram histogram = codahaleMetrics.histogram("histo");
        assertNotNull(histogram);
    }

    @Test
    public void testTimer() {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(configurationProperties);
        Timer timer = codahaleMetrics.timer("timer");
        assertNotNull(timer);
    }

    @Test
    public void testStartStopUsingDefaultConfiguration() {
        when(configurationProperties.isJmxEnabled()).thenReturn(true);
        when(configurationProperties.getMetricLogReporterPeriod()).thenReturn(5L);
        testStartStop(configurationProperties);
    }

    @Test
    public void testStartStopUsingNoJmx() {
        when(configurationProperties.isJmxEnabled()).thenReturn(false);
        when(configurationProperties.getMetricLogReporterPeriod()).thenReturn(5L);
        testStartStop(configurationProperties);
    }

    public void testStartStop(ConfigurationProperties currentConfiguration) {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(currentConfiguration);
        codahaleMetrics.histogram("histo");
        codahaleMetrics.timer("timer");
        codahaleMetrics.start();
        codahaleMetrics.stop();
    }
}

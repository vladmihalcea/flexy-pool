package com.vladmihalcea.flexy.metric.codahale;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.Histogram;
import com.vladmihalcea.flexy.metric.Timer;
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
    private Configuration configuration;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHistogram() {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(configuration);
        Histogram histogram = codahaleMetrics.histogram("histo");
        assertNotNull(histogram);
    }

    @Test
    public void testTimer() {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(configuration);
        Timer timer = codahaleMetrics.timer("timer");
        assertNotNull(timer);
    }

    @Test
    public void testStartStopUsingDefaultConfiguration() {
        when(configuration.isJmxEnabled()).thenReturn(true);
        when(configuration.getMetricLogReporterPeriod()).thenReturn(5L);
        testStartStop(configuration);
    }

    @Test
    public void testStartStopUsingNoJmx() {
        when(configuration.isJmxEnabled()).thenReturn(false);
        when(configuration.getMetricLogReporterPeriod()).thenReturn(5L);
        testStartStop(configuration);
    }

    public void testStartStop(Configuration currentConfiguration) {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(currentConfiguration);
        codahaleMetrics.histogram("histo");
        codahaleMetrics.timer("timer");
        codahaleMetrics.start();
        codahaleMetrics.stop();
    }
}

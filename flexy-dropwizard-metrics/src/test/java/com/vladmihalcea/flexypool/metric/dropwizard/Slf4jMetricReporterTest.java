package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * Slf4jMetricReporterTest - Slf4jMetricReporter Test
 *
 * @author Vlad Mihalcea
 */
public class Slf4jMetricReporterTest {

    @Mock
    private ConfigurationProperties configurationProperties;

    @Mock
    private MetricRegistry metricRegistry;

    private Slf4jMetricReporter slf4jMetricReporter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        slf4jMetricReporter = new Slf4jMetricReporter();
    }

    @Test
    public void testInitLogReportDisabled() {
        when(configurationProperties.getMetricLogReporterMillis()).thenReturn(0L);
        slf4jMetricReporter.init(configurationProperties, metricRegistry);
        slf4jMetricReporter.start();
        slf4jMetricReporter.stop();
    }

    @Test
    public void testInitLogReportEnabled() {
        when(configurationProperties.getMetricLogReporterMillis()).thenReturn(1000L);
        slf4jMetricReporter.init(configurationProperties, metricRegistry);
        slf4jMetricReporter.start();
        slf4jMetricReporter.stop();
    }
}

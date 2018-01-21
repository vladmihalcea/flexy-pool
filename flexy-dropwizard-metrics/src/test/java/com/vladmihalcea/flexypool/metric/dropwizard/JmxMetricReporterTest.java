package com.vladmihalcea.flexypool.metric.dropwizard;

import com.codahale.metrics.MetricRegistry;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * JmxMetricReporterTest - JmxMetricReporter Test
 *
 * @author Vlad Mihalcea
 */
public class JmxMetricReporterTest {

    @Mock
    private ConfigurationProperties configurationProperties;

    @Mock
    private MetricRegistry metricRegistry;

    private JmxMetricReporter jmxMetricReporter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        jmxMetricReporter = new JmxMetricReporter();
    }

    @Test
    public void testInitJmxDisabled() {
        when(configurationProperties.isJmxEnabled()).thenReturn(false);
        jmxMetricReporter.init(configurationProperties, metricRegistry);
        jmxMetricReporter.start();
        jmxMetricReporter.stop();
    }

    @Test
    public void testInitJmxEnabled() {
        when(configurationProperties.isJmxEnabled()).thenReturn(true);
        jmxMetricReporter.init(configurationProperties, metricRegistry);
        jmxMetricReporter.start();
        jmxMetricReporter.stop();
    }
}

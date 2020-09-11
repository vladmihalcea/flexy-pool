package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.strategy.DefaultNamingStrategy;
import com.vladmihalcea.flexypool.strategy.UniqueNamingStrategy;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import io.micrometer.core.instrument.Tag;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import io.micrometer.core.instrument.MeterRegistry;

import java.util.Iterator;

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

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Captor
    private ArgumentCaptor<Iterable<Tag>> tagCaptor;

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
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new DefaultNamingStrategy());
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties);
        assertSame(configurationProperties, micrometerMetrics.getConfigurationProperties());
        Histogram histogram = micrometerMetrics.histogram("histo");
        assertNotNull(histogram);
    }

    @Test
    public void testHistogramDefaultNamingStrategy() {
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new DefaultNamingStrategy());
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties, metricRegistry);
        Histogram histogram = micrometerMetrics.histogram("histo");
        assertNotNull(histogram);
        verify(metricRegistry).summary(nameCaptor.capture(), tagCaptor.capture());
        assertEquals("histo", nameCaptor.getValue());
        assertFalse(tagCaptor.getValue().iterator().hasNext());
    }

    @Test
    public void testHistogramUniquenameNamingStrategy() {
        when(configurationProperties.getUniqueName()).thenReturn("uniqueName");
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new UniqueNamingStrategy());
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties, metricRegistry);
        Histogram histogram = micrometerMetrics.histogram("histo");
        assertNotNull(histogram);
        verify(metricRegistry).summary(nameCaptor.capture(), tagCaptor.capture());
        assertEquals("flexypool_histo", nameCaptor.getValue());
        Iterator<Tag> tags = tagCaptor.getValue().iterator();
        assertTrue(tags.hasNext());
        Tag tag = tags.next();
        assertEquals("poolname", tag.getKey());
        assertEquals("uniqueName", tag.getValue());
        assertFalse(tags.hasNext());
    }

    @Test
    public void testTimer() {
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new DefaultNamingStrategy());
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties);
        Timer timer = micrometerMetrics.timer("timer");
        assertNotNull(timer);
    }

    @Test
    public void testTimerDefaultNamingStrategy() {
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new DefaultNamingStrategy());
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties, metricRegistry);
        Timer timer = micrometerMetrics.timer("timer");
        assertNotNull(timer);
        verify(metricRegistry).timer(nameCaptor.capture(), tagCaptor.capture());
        assertEquals("timer", nameCaptor.getValue());
        assertFalse(tagCaptor.getValue().iterator().hasNext());
    }

    @Test
    public void testTimerUniquenameNamingStrategy() {
        when(configurationProperties.getUniqueName()).thenReturn("uniqueName");
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new UniqueNamingStrategy());
        MicrometerMetrics micrometerMetrics = new MicrometerMetrics(configurationProperties, metricRegistry);
        Timer timer = micrometerMetrics.timer("timer");
        assertNotNull(timer);
        verify(metricRegistry).timer(nameCaptor.capture(), tagCaptor.capture());
        assertEquals("flexypool_timer", nameCaptor.getValue());
        Iterator<Tag> tags = tagCaptor.getValue().iterator();
        assertTrue(tags.hasNext());
        Tag tag = tags.next();
        assertEquals("poolname", tag.getKey());
        assertEquals("uniqueName", tag.getValue());
        assertFalse(tags.hasNext());
    }

    @Test
    public void testStartStopUsingDefaultConfiguration() {
        when(configurationProperties.isJmxEnabled()).thenReturn(true);
        when(configurationProperties.getMetricLogReporterMillis()).thenReturn(5L);
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new DefaultNamingStrategy());
        testStartStop(configurationProperties);
    }

    @Test
    public void testStartStopUsingNoJmx() {
        when(configurationProperties.isJmxEnabled()).thenReturn(false);
        when(configurationProperties.getMetricLogReporterMillis()).thenReturn(5L);
        when(configurationProperties.getMetricNamingStrategy()).thenReturn(new DefaultNamingStrategy());
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

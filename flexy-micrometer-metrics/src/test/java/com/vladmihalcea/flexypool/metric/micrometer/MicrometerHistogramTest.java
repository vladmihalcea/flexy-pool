package com.vladmihalcea.flexypool.metric.micrometer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * MicrometerHistogramTest - MicrometerHistogram Test
 *
 * @author Vlad Mihalcea
 */
public class MicrometerHistogramTest {

    private io.micrometer.core.instrument.DistributionSummary summary;

    private MicrometerHistogram histogramWrapper;

    @Before
    public void before() {
        summary = new io.micrometer.core.instrument.simple.SimpleMeterRegistry().summary("test");
        histogramWrapper = new MicrometerHistogram(summary);
    }

    @Test
    public void testUpdate() {
        assertEquals(0, summary.count());
        histogramWrapper.update(100);
        assertEquals(1, summary.count());
        assertEquals(100, summary.takeSnapshot().total(), 0.0);
    }
}

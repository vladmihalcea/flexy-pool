package com.vladmihalcea.flexypool.metric.dropwizard;

import io.dropwizard.metrics.ExponentiallyDecayingReservoir;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * DropwizardHistogramTest - DropwizardHistogram Test
 *
 * @author Vlad Mihalcea
 */
public class DropwizardHistogramTest {

    private io.dropwizard.metrics.Histogram histogram;

    private DropwizardHistogram histogramWrapper;

    @Before
    public void before() {
        histogram = new io.dropwizard.metrics.Histogram(new ExponentiallyDecayingReservoir());
        histogramWrapper = new DropwizardHistogram(histogram);
    }

    @Test
    public void testUpdate() {
        assertEquals(0, histogram.getCount());
        histogramWrapper.update(100);
        assertEquals(1, histogram.getCount());
        assertEquals(100, histogram.getSnapshot().getValues()[0]);
    }
}

package com.vladmihalcea.flexypool.metric.dropwizard;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DropwizardHistogramTest - DropwizardHistogram Test
 *
 * @author Vlad Mihalcea
 */
public class DropwizardHistogramTest {

    private com.codahale.metrics.Histogram histogram;

    private DropwizardHistogram histogramWrapper;

    @BeforeEach
    public void before() {
        histogram = new com.codahale.metrics.Histogram(new ExponentiallyDecayingReservoir());
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

package com.vladmihalcea.flexy.metric.codahale;

import com.codahale.metrics.ExponentiallyDecayingReservoir;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CodahaleHistogramTest - CodahaleHistogram Test
 *
 * @author Vlad Mihalcea
 */
public class CodahaleHistogramTest {

    private com.codahale.metrics.Histogram histogram;

    private CodahaleHistogram histogramWrapper;

    @Before
    public void before() {
        histogram = new com.codahale.metrics.Histogram(new ExponentiallyDecayingReservoir());
        histogramWrapper = new CodahaleHistogram(histogram);
    }

    @Test
    public void testUpdate() {
        assertEquals(0, histogram.getCount());
        histogramWrapper.update(100);
        assertEquals(1, histogram.getCount());
        assertEquals(100, histogram.getSnapshot().getValues()[0]);
    }
}

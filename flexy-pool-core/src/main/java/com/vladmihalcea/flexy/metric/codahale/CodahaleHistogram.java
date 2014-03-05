package com.vladmihalcea.flexy.metric.codahale;

import com.vladmihalcea.flexy.metric.Histogram;

/**
 * CodahaleTimer - CodahaleTimer
 *
 * @author Vlad Mihalcea
 */
public class CodahaleHistogram implements Histogram {

    private final com.codahale.metrics.Histogram histogram;

    public CodahaleHistogram(com.codahale.metrics.Histogram histogram) {
        this.histogram = histogram;
    }

    @Override
    public void update(long value) {
        histogram.update(value);
    }
}

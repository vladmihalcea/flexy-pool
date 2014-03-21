package com.vladmihalcea.flexy.metric.codahale;

import com.vladmihalcea.flexy.metric.Histogram;

/**
 * <code>CodahaleHistogram</code> implements the {@link com.vladmihalcea.flexy.metric.Histogram} interface by
 * delegating calls to {@link com.codahale.metrics.Histogram}
 *
 * @author	Vlad Mihalcea
 * @version	%I%, %E%
 * @since	1.0
 */
public class CodahaleHistogram implements Histogram {

    private final com.codahale.metrics.Histogram histogram;

    /**
     * Create a {@link com.codahale.metrics.Histogram} wrapper
     * @param histogram actual histogram
     */
    public CodahaleHistogram(com.codahale.metrics.Histogram histogram) {
        this.histogram = histogram;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(long value) {
        histogram.update(value);
    }
}

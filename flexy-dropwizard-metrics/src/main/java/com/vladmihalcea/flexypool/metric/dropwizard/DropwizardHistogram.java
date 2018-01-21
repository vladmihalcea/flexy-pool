package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.Histogram;

/**
 * <code>DropwizardHistogram</code> implements the {@link com.vladmihalcea.flexypool.metric.Histogram} interface by
 * delegating calls to {@link com.codahale.metrics.Histogram}
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class DropwizardHistogram implements Histogram {

    private final com.codahale.metrics.Histogram histogram;

    /**
     * Create a {@link com.codahale.metrics.Histogram} wrapper
     *
     * @param histogram actual histogram
     */
    public DropwizardHistogram(com.codahale.metrics.Histogram histogram) {
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

package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.Histogram;

/**
 * <code>DropwizardHistogram</code> implements the {@link com.vladmihalcea.flexypool.metric.Histogram} interface by
 * delegating calls to {@link io.dropwizard.metrics.Histogram}
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class DropwizardHistogram implements Histogram {

    private final io.dropwizard.metrics.Histogram histogram;

    /**
     * Create a {@link io.dropwizard.metrics.Histogram} wrapper
     *
     * @param histogram actual histogram
     */
    public DropwizardHistogram(io.dropwizard.metrics.Histogram histogram) {
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

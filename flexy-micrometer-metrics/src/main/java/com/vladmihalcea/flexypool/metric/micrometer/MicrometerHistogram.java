package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.metric.Histogram;

import io.micrometer.core.instrument.DistributionSummary;

/**
 * <code>MicrometerHistogram</code> implements the {@link Histogram} interface by
 * delegating calls to {@link DistributionSummary}
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class MicrometerHistogram implements Histogram {

    private final DistributionSummary summary;

    /**
     * Create a {@link DistributionSummary} wrapper
     *
     * @param summary actual summary
     */
    public MicrometerHistogram(DistributionSummary summary) {
        this.summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(long value) {
        summary.record(value);
    }
}

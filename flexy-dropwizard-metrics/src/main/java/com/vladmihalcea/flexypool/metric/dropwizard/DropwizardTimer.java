package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.Timer;

import java.util.concurrent.TimeUnit;

/**
 * <code>DropwizardTimer</code> implements the {@link com.vladmihalcea.flexypool.metric.Timer} interface by
 * delegating calls to {@link com.codahale.metrics.Timer}
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public class DropwizardTimer implements Timer {

    private final com.codahale.metrics.Timer timer;

    /**
     * Create a {@link com.codahale.metrics.Timer} wrapper
     *
     * @param timer actual timer
     */
    public DropwizardTimer(com.codahale.metrics.Timer timer) {
        this.timer = timer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(long duration, TimeUnit unit) {
        timer.update(duration, unit);
    }
}

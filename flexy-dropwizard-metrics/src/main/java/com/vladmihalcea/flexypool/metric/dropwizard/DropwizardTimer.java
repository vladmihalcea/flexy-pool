package com.vladmihalcea.flexypool.metric.dropwizard;

import com.vladmihalcea.flexypool.metric.Timer;

import java.util.concurrent.TimeUnit;

/**
 * <code>DropwizardTimer</code> implements the {@link com.vladmihalcea.flexypool.metric.Timer} interface by
 * delegating calls to {@link io.dropwizard.metrics.Timer}
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class DropwizardTimer implements Timer {

    private final io.dropwizard.metrics.Timer timer;

    /**
     * Create a {@link io.dropwizard.metrics.Timer} wrapper
     *
     * @param timer actual timer
     */
    public DropwizardTimer(io.dropwizard.metrics.Timer timer) {
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

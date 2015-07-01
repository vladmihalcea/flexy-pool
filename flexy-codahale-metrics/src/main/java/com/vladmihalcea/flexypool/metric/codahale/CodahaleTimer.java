package com.vladmihalcea.flexypool.metric.codahale;

import com.vladmihalcea.flexypool.metric.Timer;

import java.util.concurrent.TimeUnit;

/**
 * <code>CodahaleTimer</code> implements the {@link com.vladmihalcea.flexypool.metric.Timer} interface by
 * delegating calls to {@link com.codahale.metrics.Timer}
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class CodahaleTimer implements Timer {

    private final com.codahale.metrics.Timer timer;

    /**
     * Create a {@link com.codahale.metrics.Timer} wrapper
     *
     * @param timer actual timer
     */
    public CodahaleTimer(com.codahale.metrics.Timer timer) {
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

package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.metric.Timer;

import java.util.concurrent.TimeUnit;

/**
 * <code>MicrometerTimer</code> implements the {@link Timer} interface by
 * delegating calls to {@link io.micrometer.core.instrument.Timer}
 *
 * @author Vlad Mihalcea
 * @since 2.1.0
 */
public class MicrometerTimer implements Timer {

    private final io.micrometer.core.instrument.Timer timer;

    /**
     * Create a {@link io.micrometer.core.instrument.Timer} wrapper
     *
     * @param timer actual timer
     */
    public MicrometerTimer(io.micrometer.core.instrument.Timer timer) {
        this.timer = timer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(long duration, TimeUnit unit) {
        timer.record(duration, unit);
    }
}

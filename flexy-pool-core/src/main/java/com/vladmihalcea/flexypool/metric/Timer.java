package com.vladmihalcea.flexypool.metric;

import java.util.concurrent.TimeUnit;

/**
 * <code>Timer</code> defines the basic Timer behavior.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public interface Timer {

    /**
     * Update the timer with the given snapshot duration
     *
     * @param duration snapshot duration
     * @param unit     time unit
     */
    void update(long duration, TimeUnit unit);
}

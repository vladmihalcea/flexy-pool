package com.vladmihalcea.flexypool.metric;

import com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback;

/**
 * <code>Metrics</code> defines the basic Metrics behavior.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public interface Metrics extends LifeCycleCallback {

    /**
     * Get histogram
     *
     * @param name histogram name
     * @return Histogram
     */
    Histogram histogram(String name);

    /**
     * Get timer
     *
     * @param name timer name
     * @return Timer
     */
    Timer timer(String name);
}

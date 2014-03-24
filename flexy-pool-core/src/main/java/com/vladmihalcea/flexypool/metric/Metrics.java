package com.vladmihalcea.flexypool.metric;

import com.vladmihalcea.flexypool.lifecycle.LifeCycleAware;

/**
 * <code>Metrics</code> defines the basic Metrics behavior..
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public interface Metrics extends LifeCycleAware {

    /**
     * Get histogram
     *
     * @param name histogram name
     */
    Histogram histogram(String name);

    /**
     * Get timer
     *
     * @param name timer name
     */
    Timer timer(String name);
}

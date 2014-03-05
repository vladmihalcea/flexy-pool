package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.lifecycle.LifeCycleAware;

/**
 * Metrics - This interface defines a metrics basic behavior.
 *
 * @author Vlad Mihalcea
 */
public interface Metrics extends LifeCycleAware {

    /**
     * Get histogram
     *
     * @param name  histogram name
     */
    Histogram histogram(String name);

    /**
     * Get timer
     *
     * @param name  timer name
     */
    Timer timer(String name);
}

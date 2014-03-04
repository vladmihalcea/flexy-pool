package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.lifecycle.LifeCycleAware;

import java.util.concurrent.TimeUnit;

/**
 * Metrics - This interface defines a metrics basic behavior.
 *
 * @author Vlad Mihalcea
 */
public interface Metrics extends LifeCycleAware {

    /**
     * Update the given histogram
     *
     * @param name  histogram name
     * @param value histogram instant value
     */
    void updateHistogram(String name, long value);

    /**
     * Update the given timer
     *
     * @param name  timer name
     * @param value timer instant value
     */
    void updateTimer(String name, long value, TimeUnit timeUnit);
}

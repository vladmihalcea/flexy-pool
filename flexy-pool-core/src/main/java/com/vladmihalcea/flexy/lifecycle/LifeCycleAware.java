package com.vladmihalcea.flexy.lifecycle;

/**
 * LifeCycleAware - LifeCycleAware
 *
 * @author Vlad Mihalcea
 */
public interface LifeCycleAware {

    /**
     * Starting lifecycle callback.
     */
    void start();

    /**
     * Stopping lifecycle callback.
     */
    void stop();
}

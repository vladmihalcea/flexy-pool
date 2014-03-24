package com.vladmihalcea.flexypool.lifecycle;

/**
 * <code>LifeCycleAware</code> defines lifecycle listening hooks.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public interface LifeCycleAware {

    /**
     * Starting callback.
     */
    void start();

    /**
     * Stopping callback.
     */
    void stop();
}

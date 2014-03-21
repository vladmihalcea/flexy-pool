package com.vladmihalcea.flexy.metric;

/**
 * <code>Histogram</code> defines the basic Histogram behavior..
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public interface Histogram {

    /**
     * Update the histogram with the given snapshot value
     *
     * @param value snapshot value
     */
    void update(long value);
}

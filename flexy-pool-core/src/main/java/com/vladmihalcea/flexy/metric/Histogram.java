package com.vladmihalcea.flexy.metric;

/**
 * Histogram - Histogram
 *
 * @author Vlad Mihalcea
 */
public interface Histogram {

   void update(long value);
}

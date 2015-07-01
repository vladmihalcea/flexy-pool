package com.vladmihalcea.flexypool.metric;

/**
 * <code>MetricsFactoryService</code> - MetricsFactory Loader Service Provider Interface
 *
 * @author Vlad Mihalcea
 */
public interface MetricsFactoryService {

    /**
     * Load MetricsFactory implementation
     *
     * @return load a given MetricsFactory implementation
     */
    MetricsFactory load();
}

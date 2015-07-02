package com.vladmihalcea.flexypool.metric;

/**
 * <code>MetricsFactoryService</code> - MetricsFactory Loader Service Provider Interface
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public interface MetricsFactoryService {

    /**
     * Load MetricsFactory implementation
     *
     * @return load a given MetricsFactory implementation
     */
    MetricsFactory load();
}

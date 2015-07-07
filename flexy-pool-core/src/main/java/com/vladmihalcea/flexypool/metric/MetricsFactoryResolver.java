package com.vladmihalcea.flexypool.metric;

import java.util.ServiceLoader;

/**
 * <code>MetricsFactoryResolver</code> - MetricsFactory Resolver
 *
 * @author Vlad Mihalcea
 * @since 1.2.2
 */
public final class MetricsFactoryResolver {

    public static final MetricsFactoryResolver INSTANCE = new MetricsFactoryResolver();

    private ServiceLoader<MetricsFactoryService> serviceLoader =
            ServiceLoader.load(MetricsFactoryService.class);

    private MetricsFactoryResolver() {
    }

    /**
     * Resolve MetricsFactory from the Service Provider Interface configuration
     *
     * @return MetricsFactory
     */
    public MetricsFactory resolve() {
        for (MetricsFactoryService metricsFactoryService : serviceLoader) {
            MetricsFactory metricsFactory = metricsFactoryService.load();
            if (metricsFactory != null) {
                return metricsFactory;
            }
        }
        throw new IllegalStateException("No MetricsFactory could be loaded!");
    }
}

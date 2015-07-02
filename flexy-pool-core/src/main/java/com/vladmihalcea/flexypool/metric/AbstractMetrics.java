package com.vladmihalcea.flexypool.metric;

import com.vladmihalcea.flexypool.util.ConfigurationProperties;

/**
 * <code>AbstractMetrics</code> implements the {@link com.vladmihalcea.flexypool.metric.Metrics} interface and
 * adds the Configuration property.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public abstract class AbstractMetrics implements Metrics {

    private final ConfigurationProperties configurationProperties;

    /**
     * Create {@link AbstractMetrics} from the given {@link ConfigurationProperties}
     *
     * @param configurationProperties configuration properties
     */
    protected AbstractMetrics(ConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    /**
     * Get {@link ConfigurationProperties}
     *
     * @return configuration properties
     */
    public ConfigurationProperties getConfigurationProperties() {
        return configurationProperties;
    }
}

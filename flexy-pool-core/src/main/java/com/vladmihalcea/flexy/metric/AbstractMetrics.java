package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.util.ConfigurationProperties;

/**
 * <code>AbstractMetrics</code> implements the {@link com.vladmihalcea.flexy.metric.Metrics} interface and
 * adds the Configuration property.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public abstract class AbstractMetrics implements Metrics {

    private final ConfigurationProperties configurationProperties;

    protected AbstractMetrics(ConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public ConfigurationProperties getConfigurationProperties() {
        return configurationProperties;
    }
}

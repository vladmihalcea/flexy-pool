package com.vladmihalcea.flexypool.metric;

import com.vladmihalcea.flexypool.util.ConfigurationProperties;

/**
 * MetricsBuilder - Metrics Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface MetricsBuilder {

    Metrics build(ConfigurationProperties configurationProperties);
}

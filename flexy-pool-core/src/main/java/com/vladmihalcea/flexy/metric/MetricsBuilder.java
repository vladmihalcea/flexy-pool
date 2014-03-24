package com.vladmihalcea.flexy.metric;

import com.vladmihalcea.flexy.util.ConfigurationProperties;

/**
 * MetricsBuilder - Metrics Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface MetricsBuilder {

    Metrics build(ConfigurationProperties configurationProperties);
}

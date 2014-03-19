package com.vladmihalcea.flexy.builder;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.Metrics;

/**
 * MetricsBuilder - Metrics Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface MetricsBuilder {

    Metrics build(Configuration configuration);
}

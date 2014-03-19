package com.vladmihalcea.flexy.factory;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.Metrics;

/**
 * Factory - Metrics Configuration based factory
 *
 * @author Vlad Mihalcea
 */
public interface MetricsFactory {

    Metrics newInstance(Configuration configuration);
}

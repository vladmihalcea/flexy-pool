package com.vladmihalcea.flexy.builder;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.strategy.ConnectionAcquiringStrategy;

/**
 * Factory - ConnectionAcquiringStrategy Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquiringStrategyBuilder<T extends ConnectionAcquiringStrategy> {

    T build(Configuration configuration);
}

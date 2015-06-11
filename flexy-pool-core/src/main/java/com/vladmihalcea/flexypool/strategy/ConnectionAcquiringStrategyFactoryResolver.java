package com.vladmihalcea.flexypool.strategy;

import javax.sql.DataSource;
import java.util.List;

/**
 * ConnectionAcquiringStrategyFactoryResolver - {@link ConnectionAcquiringStrategyFactory} Resolver
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquiringStrategyFactoryResolver<T extends DataSource> {

    /**
     * Resolve the list of {@link ConnectionAcquiringStrategyFactory}
     */
    List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>> resolveFactories();
}

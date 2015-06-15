package com.vladmihalcea.flexypool.strategy;

import javax.sql.DataSource;
import java.util.List;

/**
 * <code>ConnectionAcquiringStrategyFactoryResolver</code> - {@link ConnectionAcquiringStrategyFactory} Resolver
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.2
 */
public interface ConnectionAcquiringStrategyFactoryResolver<T extends DataSource> {

    /**
     * Resolve the list of {@link ConnectionAcquiringStrategyFactory}
     */
    List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>> resolveFactories();
}

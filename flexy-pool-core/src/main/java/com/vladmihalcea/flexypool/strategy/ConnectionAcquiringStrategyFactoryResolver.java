package com.vladmihalcea.flexypool.strategy;

import javax.sql.DataSource;
import java.util.List;

/**
 * <code>ConnectionAcquiringStrategyFactoryResolver</code> - The resolver is used to provide a
 * List of {@link ConnectionAcquiringStrategyFactory} for the declarative configuration support.
 * This way we can only provide a class name and FlexyPool can retrieve all associated strategies.
 *
 * @author Vlad Mihalcea
 * @since 1.2
 */
public interface ConnectionAcquiringStrategyFactoryResolver<T extends DataSource> {

    /**
     * Resolve the list of {@link ConnectionAcquiringStrategyFactory}
     */
    List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>> resolveFactories();
}

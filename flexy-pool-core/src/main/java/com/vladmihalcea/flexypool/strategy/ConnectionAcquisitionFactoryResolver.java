package com.vladmihalcea.flexypool.strategy;

import javax.sql.DataSource;
import java.util.List;

/**
 * <code>ConnectionAcquisitionFactoryResolver</code> - The resolver is used to provide a
 * List of {@link ConnectionAcquisitionStrategyFactory} for the declarative configuration support.
 * This way we can only provide a class name and FlexyPool can retrieve all associated strategies.
 *
 * @author Vlad Mihalcea
 * @since 1.2
 */
public interface ConnectionAcquisitionFactoryResolver<T extends DataSource> {

    /**
     * Resolve the list of {@link ConnectionAcquisitionStrategyFactory}
     *
     * @return list of {@link ConnectionAcquisitionStrategyFactory}
     */
    List<ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, T>> resolveFactories();
}

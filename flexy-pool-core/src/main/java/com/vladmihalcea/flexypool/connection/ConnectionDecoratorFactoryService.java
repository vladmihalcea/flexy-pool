package com.vladmihalcea.flexypool.connection;

/**
 * <code>ConnectionDecoratorFactoryService</code> - ConnectionDecoratorFactory Loader Service Provider Interface.
 * There can be multiple such services available at Runtime and the highest loading index that can provide a non null
 * {@link ConnectionDecoratorFactory} is going to be used.
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public interface ConnectionDecoratorFactoryService {

    /**
     * Get loading index
     *
     * @return loading index
     */
    int loadingIndex();

    /**
     * Load a ConnectionDecoratorFactory implementation
     *
     * @return load a given ConnectionDecoratorFactory implementation
     */
    ConnectionDecoratorFactory load();
}

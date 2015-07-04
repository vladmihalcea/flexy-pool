package com.vladmihalcea.flexypool.connection;

/**
 * <code>ConnectionDecoratorFactoryService</code> - ConnectionDecoratorFactory Loader Service Provider Interface
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public interface ConnectionDecoratorFactoryService {

    /**
     * Get loading index
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

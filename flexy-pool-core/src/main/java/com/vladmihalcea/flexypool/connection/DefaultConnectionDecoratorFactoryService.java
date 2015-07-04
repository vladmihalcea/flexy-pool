package com.vladmihalcea.flexypool.connection;

/**
 * <code>DefaultConnectionDecoratorFactoryService</code> - Default ConnectionDecoratorFactory Service
 *
 * @author Vlad Mihalcea
 * @version 1.2.3
 */
public class DefaultConnectionDecoratorFactoryService implements ConnectionDecoratorFactoryService {

    public static final int LOADING_INDEX = 0x16;

    /**
     * {@inheritDoc}
     */
    @Override
    public int loadingIndex() {
        return LOADING_INDEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionDecoratorFactory load() {
        return new ConnectionDecoratorFactory();
    }
}

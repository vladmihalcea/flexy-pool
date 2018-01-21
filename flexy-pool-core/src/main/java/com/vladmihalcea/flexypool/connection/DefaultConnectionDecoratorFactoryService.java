package com.vladmihalcea.flexypool.connection;

/**
 * <code>DefaultConnectionDecoratorFactoryService</code> - The default {@link ConnectionDecoratorFactory} Service is using the
 * JDK 1.8 {@link java.sql.Connection} interface version.
 *
 * @author Vlad Mihalcea
 * @version 1.2.3
 */
public class DefaultConnectionDecoratorFactoryService implements ConnectionDecoratorFactoryService {

    public static final int LOADING_INDEX = 0x18;

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

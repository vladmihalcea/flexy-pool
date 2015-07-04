package com.vladmihalcea.flexypool.connection;

/**
 * <code>Java7ConnectionDecoratorFactoryService</code> - Java 1.7 ConnectionDecoratorFactory Service
 *
 * @author Vlad Mihalcea
 * @version 1.2.3
 */
public class Java7ConnectionDecoratorFactoryService implements ConnectionDecoratorFactoryService {

    public static final int JAVA_6_LOADING_INDEX = 0x17;

    /**
     * {@inheritDoc}
     */
    @Override
    public int loadingIndex() {
        return JAVA_6_LOADING_INDEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionDecoratorFactory load() {
        return new Java7ConnectionDecoratorFactory();
    }
}

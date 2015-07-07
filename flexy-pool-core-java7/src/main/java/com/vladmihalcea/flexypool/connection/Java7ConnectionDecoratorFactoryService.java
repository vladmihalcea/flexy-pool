package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;

import java.sql.Connection;

/**
 * <code>Java7ConnectionDecoratorFactoryService</code> - Java 1.7 ConnectionDecoratorFactory Service
 *
 * @author Vlad Mihalcea
 * @version 1.2.3
 */
public class Java7ConnectionDecoratorFactoryService extends DefaultConnectionDecoratorFactoryService {

    public static final int LOADING_INDEX = 0x17;

    public static final String AVAILABLE_METHOD = "getSchema";

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
        return (ReflectionUtils.hasMethod(Connection.class, AVAILABLE_METHOD)) ?
            new Java7ConnectionDecoratorFactory() :
            super.load();
    }
}
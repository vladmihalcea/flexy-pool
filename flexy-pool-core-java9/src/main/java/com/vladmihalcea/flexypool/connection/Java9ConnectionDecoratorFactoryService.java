package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;

import java.sql.Connection;

/**
 * <code>Java9ConnectionDecoratorFactoryService</code> - Java 1.9 ConnectionDecoratorFactory Service
 *
 * @author Vlad Mihalcea
 * @version 2.0.0
 */
public class Java9ConnectionDecoratorFactoryService extends DefaultConnectionDecoratorFactoryService {

    public static final int LOADING_INDEX = 0x19;

    public static final String AVAILABLE_METHOD = "beginRequest";

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
            new Java9ConnectionDecoratorFactory() :
            super.load();
    }
}
package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * <code>Java7ConnectionDecoratorFactory</code> - Java 1.7 ConnectionDecorator Factory
 *
 * @author Vlad Mihalcea
 */
public class Java7ConnectionDecoratorFactory extends ConnectionDecoratorFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Connection proxyConnection(Connection target, ConnectionCallback callback) {
        return new Java7ConnectionDecorator(target, callback);
    }
}

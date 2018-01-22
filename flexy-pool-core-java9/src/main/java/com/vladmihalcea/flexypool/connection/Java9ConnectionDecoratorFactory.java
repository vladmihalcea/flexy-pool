package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * <code>Java9ConnectionDecoratorFactory</code> - Java 1.9 ConnectionDecorator Factory
 *
 * @author Vlad Mihalcea
 * @version 2.0.0
 */
public class Java9ConnectionDecoratorFactory extends ConnectionDecoratorFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Connection proxyConnection(Connection target, ConnectionCallback callback) {
        return new Java9ConnectionDecorator( target, callback);
    }
}

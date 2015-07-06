package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * <code>ConnectionDecoratorFactory</code> creates {@link ConnectionDecorator} instances.
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public class ConnectionDecoratorFactory extends ConnectionProxyFactory {

    public static final ConnectionProxyFactory INSTANCE = new ConnectionDecoratorFactory();

    /**
     * Create a {@link ConnectionDecorator} delegate to the actual target {@link Connection}
     *
     * @param target   connection to proxy
     * @param callback attaching connection lifecycle listener
     * @return {@link Connection} delegate
     */
    @Override
    protected Connection proxyConnection(Connection target, ConnectionCallback callback) {
        return new ConnectionDecorator(target, callback);
    }
}

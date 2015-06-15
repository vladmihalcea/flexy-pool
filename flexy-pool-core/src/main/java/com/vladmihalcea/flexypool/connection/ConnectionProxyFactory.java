package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * <code>ConnectionProxyFactory</code> - Builds Connection Proxies
 *
 * @author Vlad Mihalcea
 */
public abstract class ConnectionProxyFactory {

    /**
     * Creates a ConnectionProxy for the specified target and attaching the
     * following callback.
     * @param target connection to proxy
     * @param connectionPoolCallback attaching connection lifecycle listener
     * @return ConnectionProxy
     */
    public Connection newInstance(Connection target, ConnectionPoolCallback connectionPoolCallback) {
        return proxyConnection(target, new ConnectionCallback(connectionPoolCallback));
    }

    /**
     * Proxy the given connection
     * @param target connection to proxy
     * @param callback attaching connection lifecycle listener
     * @return proxy connection
     */
    protected abstract Connection proxyConnection(Connection target, ConnectionCallback callback);
}

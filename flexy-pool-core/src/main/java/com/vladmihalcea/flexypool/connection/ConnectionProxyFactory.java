package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * ConnectionProxyFactory - Builds Connection Proxies
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
        ConnectionCallback connectionCallback = new ConnectionCallback(connectionPoolCallback);
        Connection proxy = proxyConnection(target, connectionCallback);
        connectionCallback.init();
        return proxy;
    }

    /**
     * Proxy the given connection
     * @param target connection to proxy
     * @param callback attaching connection lifecycle listener
     * @return proxy connection
     */
    protected abstract Connection proxyConnection(Connection target, ConnectionCallback callback);
}

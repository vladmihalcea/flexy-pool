package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * ConnectionProxyFactory - Builds Connection Proxies
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionProxyFactory {

    /**
     * Creates a connection proxy for the specified target and attaching the
     * following callback.
     * @param target connection to proxy
     * @param callback attaching connection lifecycle listener
     * @return connection proxy
     */
    Connection newInstance(Connection target, ConnectionCallback callback);
}

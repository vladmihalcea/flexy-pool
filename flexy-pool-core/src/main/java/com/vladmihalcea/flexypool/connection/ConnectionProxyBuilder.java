package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * ConnectionProxyBuilder - Builds Connection Proxies
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionProxyBuilder {

    /**
     * Builds a connection proxy for the specified target and attaching the
     * following callback.
     * @param target connection to proxy
     * @param callback attaching connection lifecycle listener
     * @return connection proxy
     */
    Connection build(Connection target, ConnectionCallback callback);
}

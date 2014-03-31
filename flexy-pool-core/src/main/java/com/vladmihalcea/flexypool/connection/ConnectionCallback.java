package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;

/**
 * <code>ConnectionCallback</code> defines executing callbacks upon connection acquire or release.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public interface ConnectionCallback {

    void acquire(Connection connection);

    void release(Connection connection, long durationNanos);
}

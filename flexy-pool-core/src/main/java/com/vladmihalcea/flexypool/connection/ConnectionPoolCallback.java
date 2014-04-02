package com.vladmihalcea.flexypool.connection;

/**
 * <code>ConnectionPoolCallback</code> defines executing callbacks upon connection acquireConnection or releaseConnection.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public interface ConnectionPoolCallback {

    /**
     * Connection acquire callback.
     */
    void acquireConnection();

    /**
     * Connection release callback.
     * @param leaseDurationNanos lease duration nanos
     */
    void releaseConnection(long leaseDurationNanos);
}

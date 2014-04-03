package com.vladmihalcea.flexypool.connection;

/**
 * <code>ConnectionCallback</code> defines {@link java.sql.Connection} lifecycle callbacks.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class ConnectionCallback {

    private final ConnectionPoolCallback connectionPoolCallback;

    private final long startNanos = System.nanoTime();

    public ConnectionCallback(ConnectionPoolCallback connectionPoolCallback) {
        this.connectionPoolCallback = connectionPoolCallback;
        this.connectionPoolCallback.acquireConnection();
    }

    /**
     * Connection close callback.
     */
    public void close() {
        long endNanos = System.nanoTime();
        long durationNanos = endNanos - startNanos;
        connectionPoolCallback.releaseConnection(durationNanos);
    }
}

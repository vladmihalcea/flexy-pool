package com.vladmihalcea.flexypool.connection;

/**
 * <code>ConnectionCallback</code> defines {@link java.sql.Connection} lifecycle callbacks.
 * It's used by the Connection decorator/proxy to execute a custom logic upon closing the connection.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class ConnectionCallback {

    private final ConnectionPoolCallback connectionPoolCallback;

    private final long startNanos = System.nanoTime();

    /**
     * Create Connection Callback with {@link ConnectionPoolCallback} hook
     *
     * @param connectionPoolCallback {@link ConnectionPoolCallback} hook
     */
    public ConnectionCallback(ConnectionPoolCallback connectionPoolCallback) {
        this.connectionPoolCallback = connectionPoolCallback;
        this.connectionPoolCallback.acquireConnection();
    }

    /**
     * Calculates the connection lease nanos and propagates it to the connection pool callback.
     */
    public void close() {
        long endNanos = System.nanoTime();
        long durationNanos = endNanos - startNanos;
        connectionPoolCallback.releaseConnection(durationNanos);
    }
}

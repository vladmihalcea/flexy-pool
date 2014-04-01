package com.vladmihalcea.flexypool.connection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * <code>DynamicProxyInvocationHandler</code> is the {@link java.sql.Connection} method interceptor.
 * It calls the <code>ConnectionCallback</code> on connection acquire or release.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class DynamicProxyInvocationHandler implements InvocationHandler {

    public static final String CLOSE_METHOD_NAME = "close";

    public static ConnectionProxyFactory FACTORY = new ConnectionProxyFactory() {
        @Override
        public Connection newInstance(Connection target, ConnectionCallback callback) {
            DynamicProxyInvocationHandler invocationHandler = new DynamicProxyInvocationHandler(target, callback);
            callback.acquire(invocationHandler.connection);
            return invocationHandler.connection;
        }
    };

    private final Connection targetConnection;
    private final ConnectionCallback connectionCallback;
    private final Connection connection;
    private final long startNanos;

    private DynamicProxyInvocationHandler(Connection targetConnection, ConnectionCallback connectionCallback) {
        this.targetConnection = targetConnection;
        this.connectionCallback = connectionCallback;
        this.connection = (Connection)
                Proxy.newProxyInstance(
                        this.getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        this);
        this.startNanos = System.nanoTime();
    }

    /**
     * Intercepts all {@link java.sql.Connection} method calls.
     * @param proxy originating proxy
     * @param method called method
     * @param args called method arguments
     * @return returned object
     * @throws Throwable in case any error occurred
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (CLOSE_METHOD_NAME.equals(method.getName())) {
            close();
        }
        return method.invoke(targetConnection, args);
    }

    /**
     * Close method interceptor.
     */
    private void close() {
        long endNanos = System.nanoTime();
        long durationNanos = endNanos - startNanos;
        this.connectionCallback.release(connection, durationNanos);
    }
}

package com.vladmihalcea.flexypool.connection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * <code>JdkConnectionProxyFactory</code> is the Dynamic Proxy {@link java.sql.Connection} factory.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class JdkConnectionProxyFactory extends ConnectionProxyFactory {

    public static final ConnectionProxyFactory INSTANCE = new JdkConnectionProxyFactory();

    /**
     * <code>ConnectionInvocationHandler</code> is the {@link java.sql.Connection} method interceptor.
     * It calls the <code>ConnectionCallback</code> on connection acquireConnection or releaseConnection.
     *
     * @author Vlad Mihalcea
     * @since 1.0
     */
    private static class ConnectionInvocationHandler implements InvocationHandler {

        public static final String CLOSE_METHOD_NAME = "close";

        private final Connection target;
        private final ConnectionCallback connectionCallback;

        public ConnectionInvocationHandler(Connection target, ConnectionCallback connectionCallback) {
            this.target = target;
            this.connectionCallback = connectionCallback;
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
                connectionCallback.close();
            }
            return method.invoke(target, args);
        }
    }

    @Override
    protected Connection proxyConnection(Connection target, ConnectionCallback callback) {
        return (Connection)
                Proxy.newProxyInstance(
                        this.getClass().getClassLoader(),
                        new Class[]{Connection.class},
                        new ConnectionInvocationHandler(target, callback));
    }
}

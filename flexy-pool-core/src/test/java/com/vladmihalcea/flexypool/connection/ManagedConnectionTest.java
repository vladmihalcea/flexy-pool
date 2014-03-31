package com.vladmihalcea.flexypool.connection;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import com.vladmihalcea.flexypool.util.TestUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * ManagedConnectionTest - ManagedConnection Test
 *
 * @author Vlad Mihalcea
 */
public class ManagedConnectionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedConnectionTest.class);

    private static Map<Class<?>, Object> classToPrimitives = new HashMap<Class<?>, Object>() {{
        put(Boolean.TYPE, false);
        put(Byte.TYPE, 0);
        put(Short.TYPE, 0);
        put(Integer.TYPE, 0);
        put(Long.TYPE, 0L);
        put(Float.TYPE, 0F);
        put(Double.TYPE, 0D);
        put(Void.TYPE, null);
    }};

    private static Map<Class<?>, Object> classToFinalObjects = new HashMap<Class<?>, Object>() {{
        put(String.class, "");
        put(String[].class, new String[]{});
        put(int[].class, new int[]{});
        put(Object[].class, new Object[]{});
        put(Class.class, Object.class);
    }};

    private static MetricRegistry metricRegistry = new MetricRegistry();
    private static Slf4jReporter logReporter = Slf4jReporter
            .forRegistry(metricRegistry)
            .outputTo(LOGGER)
            .build();

    private static Timer noProxyHistogram = metricRegistry.timer("no-proxy");
    private static Timer proxyHistogram = metricRegistry.timer("proxy");

    static {
        logReporter.start(30, TimeUnit.SECONDS);
    }

    private final Connection targetConnection = Mockito.mock(Connection.class);

    private final InvocationHandler invocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(targetConnection, args);
        }
    };
    private final Connection proxy = (Connection) Proxy.newProxyInstance(this.getClass().getClassLoader(),
            new Class[]{Connection.class}, invocationHandler);

    private ManagedConnection managedConnection = new ManagedConnection(targetConnection);

    @Test
    public void testAllMethodsAreInvoked() {
        invokeAllMethods(managedConnection);
    }

    @Test
    public void testPerformance() {
        if (TestUtils.isPerformanceTesting()) {
            long durationMillis = 30 * 1000;
            timeAllMethodsForDuration(new Callable() {
                @Override
                public Object call() throws Exception {
                    callConnectionMethods(managedConnection, noProxyHistogram);
                    return null;
                }
            }, durationMillis);
            timeAllMethodsForDuration(new Callable() {
                @Override
                public Object call() throws Exception {
                    callConnectionMethods(proxy, proxyHistogram);
                    return null;
                }
            }, durationMillis);
        }
    }

    private void callConnectionMethods(Connection connection, Timer timer) throws SQLException {
        long startNanos = System.nanoTime();
        try {
            managedConnection.getMetaData();
            managedConnection.setSchema("schema");
            managedConnection.abort(null);
        } finally {
            long endNanos = System.nanoTime();
            timer.update((endNanos - startNanos), TimeUnit.NANOSECONDS);
        }
    }

    private void invokeAllMethods(Connection connection) {
        for (Method method : Connection.class.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (!parameterType.isPrimitive()) {
                    Object finalObject = classToFinalObjects.get(parameterType);
                    parameters[i] = finalObject != null ? finalObject : Mockito.mock(parameterType);
                } else {
                    parameters[i] = classToPrimitives.get(parameterType);
                }
            }
            ReflectionUtils.invoke(connection, ReflectionUtils.getMethod(connection, method.getName(), parameterTypes), parameters);
        }
    }

    private void timeAllMethodsForDuration(Callable callable, long durationMillis) {
        long startMillis = System.currentTimeMillis();
        int i = 0;
        while (true) {
            long endMillis = System.currentTimeMillis();
            if(i % 1000 == 0) {
                LOGGER.info("Iteration: {}, total millis {}!", i, endMillis - startMillis);
                if((endMillis - startMillis) > durationMillis) {
                    break;
                }
            }
            try {
                callable.call();
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            i++;
        }
    }
}

package com.vladmihalcea.flexypool.connection;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * ConnectionProxyFactoryPerformanceTest - ConnectionProxyFactory Performance Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionProxyFactoryPerformanceTest extends ConnectionDecoratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProxyFactoryPerformanceTest.class);

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
    private final ConnectionCallback connectionCallback = Mockito.mock(ConnectionCallback.class);

    private final InvocationHandler invocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(targetConnection, args);
        }
    };
    private final Connection proxy = (Connection) Proxy.newProxyInstance(this.getClass().getClassLoader(),
            new Class[]{Connection.class}, invocationHandler);

    private ConnectionDecorator connectionDecorator = new ConnectionDecorator(targetConnection, connectionCallback);

    @Test
    public void testPerformance() {
        if (TestUtils.isPerformanceTesting()) {
            long durationMillis = 30 * 1000;
            timeAllMethodsForDuration(new Callable() {
                @Override
                public Object call() throws Exception {
                    callConnectionMethods(noProxyHistogram);
                    return null;
                }
            }, durationMillis);
            timeAllMethodsForDuration(new Callable() {
                @Override
                public Object call() throws Exception {
                    callConnectionMethods(proxyHistogram);
                    return null;
                }
            }, durationMillis);
        }
    }

    private void callConnectionMethods(Timer timer) throws SQLException {
        long startNanos = System.nanoTime();
        try {
            connectionDecorator.getMetaData();
            String javaVersion = System.getProperty("java.version");
            if (javaVersion.contains("1.7") || javaVersion.contains("1.8")) {
                connectionDecorator.setSchema("schema");
                connectionDecorator.abort(null);
            }
        } finally {
            long endNanos = System.nanoTime();
            timer.update((endNanos - startNanos), TimeUnit.NANOSECONDS);
        }
    }

    private void timeAllMethodsForDuration(Callable callable, long durationMillis) {
        long startMillis = System.currentTimeMillis();
        int i = 0;
        while (true) {
            long endMillis = System.currentTimeMillis();
            if (i % 1000 == 0) {
                LOGGER.info("Iteration: {}, total millis {}!", i, endMillis - startMillis);
                if ((endMillis - startMillis) > durationMillis) {
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

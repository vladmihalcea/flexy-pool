package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.connection.ConnectionCallback;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.event.ConnectionAcquireTimeoutEvent;
import com.vladmihalcea.flexypool.event.Event;
import com.vladmihalcea.flexypool.event.EventListener;
import com.vladmihalcea.flexypool.event.EventListenerResolver;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactoryResolver;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.util.JndiTestUtils;
import com.vladmihalcea.flexypool.util.MockDataSource;
import com.vladmihalcea.flexypool.util.PropertiesTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * PropertyLoaderTest - PropertyLoader Test
 *
 * @author Vlad Mihalcea
 */
public class PropertyLoaderTest {

    @Before
    public void init() {
        PropertiesTestUtils.init();
    }

    @Test
    public void testLoadPropertiesWhenFileIsMissing() {
        PropertyLoader propertyLoader = new PropertyLoader();
        assertTrue(PropertiesTestUtils.getProperties(propertyLoader).isEmpty());
    }

    @Test
    public void testLoadPropertiesFromURLSystemProperty() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey(), "jdbc/DS");
            File propertiesFile = PropertiesTestUtils.setProperties(properties);
            try {
                System.setProperty(PropertyLoader.PROPERTIES_FILE_PATH, propertiesFile.toURI().toURL().toString());
                PropertyLoader propertyLoader = new PropertyLoader();
                assertEquals("jdbc/DS", propertyLoader.getUniqueName());
            } finally {
                System.clearProperty(PropertyLoader.PROPERTIES_FILE_PATH);
            }
        } catch (IOException e) {
            fail("Can't save/load properties");
        }
    }

    @Test
    public void testLoadPropertiesFromFileSystemProperty() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey(), "jdbc/DS");
            File propertiesFile = PropertiesTestUtils.setProperties(properties);
            try {
                System.setProperty(PropertyLoader.PROPERTIES_FILE_PATH, propertiesFile.getAbsolutePath());
                PropertyLoader propertyLoader = new PropertyLoader();
                assertEquals("jdbc/DS", propertyLoader.getUniqueName());
            } finally {
                System.clearProperty(PropertyLoader.PROPERTIES_FILE_PATH);
            }
        } catch (IOException e) {
            fail("Can't save/load properties");
        }
    }

    @Test
    public void testLoadPropertiesFromNestedClassPathSystemProperty() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey(), "jdbc/DS");
            File propertiesFile = PropertiesTestUtils.setProperties(properties);
            String resourceFolder = "nested";
            File newFileFolder = new File(propertiesFile.getParentFile().getAbsolutePath() + "/" + resourceFolder);
            newFileFolder.mkdirs();
            String resourceFile = "fp.properties";
            File newFile = new File(newFileFolder, resourceFile);
            propertiesFile.renameTo(newFile);
            String resourcePath = resourceFolder + "/" + resourceFile;
            try {
                System.setProperty(PropertyLoader.PROPERTIES_FILE_PATH, resourcePath);
                PropertyLoader propertyLoader = new PropertyLoader();
                assertEquals("jdbc/DS", propertyLoader.getUniqueName());
            } finally {
                System.clearProperty(PropertyLoader.PROPERTIES_FILE_PATH);
                newFile.delete();
                newFileFolder.delete();
            }
        } catch (IOException e) {
            fail("Can't save/load properties");
        }
    }

    @Test
    public void testLoadPropertiesWhenFileExistsButIsEmpty() {
        try {
            PropertiesTestUtils.setProperties(new Properties());
            PropertyLoader propertyLoader = new PropertyLoader();
            assertTrue(PropertiesTestUtils.getProperties(propertyLoader).isEmpty());
            try {
                propertyLoader.getDataSource();
                fail("The DATA_SOURCE_CLASS_NAME property should be missing");
            } catch (IllegalArgumentException e) {
                assertEquals("The DATA_SOURCE_CLASS_NAME property is mandatory!", e.getMessage());
            }
            assertNull(propertyLoader.getMetricsFactory());
            assertNull(propertyLoader.getPoolAdapterFactory());
            assertTrue(propertyLoader.getConnectionAcquiringStrategyFactories().isEmpty());
        } catch (IOException e) {
            fail("Can't save/load properties");
        }
    }

    @Test
    public void testLoadPropertiesWhenFileExistsAndContainsAllConfigs() {
        try {
            Properties properties = new Properties();
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey(), "jdbc/DS");
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_CLASS_NAME.getKey(), MockDataSource.class.getName());
            properties.put("flexy.pool.data.source.property.user", "sa");
            properties.put("flexy.pool.data.source.property.password", "admin");
            properties.put("flexy.pool.data.source.property.url", "jdbc://host:1234/database");
            properties.put(PropertyLoader.PropertyKey.POOL_ADAPTER_FACTORY.getKey(), MockPoolAdapterFactory.class.getName());
            properties.put(PropertyLoader.PropertyKey.POOL_METRICS_FACTORY.getKey(), MockMetricsFactory.class.getName());
            properties.put(PropertyLoader.PropertyKey.POOL_METRICS_REPORTER_LOG_MILLIS.getKey(), "123");
            properties.put(PropertyLoader.PropertyKey.POOL_METRICS_REPORTER_JMX_AUTO_START.getKey(), "true");
            properties.put(PropertyLoader.PropertyKey.POOL_METRICS_REPORTER_JMX_ENABLE.getKey(), "false");
            properties.put(PropertyLoader.PropertyKey.POOL_METRICS_FACTORY.getKey(), MockMetricsFactory.class.getName());
            properties.put(PropertyLoader.PropertyKey.POOL_CONNECTION_PROXY_FACTORY.getKey(), MockConnectionProxyFactory.class.getName());
            properties.put(PropertyLoader.PropertyKey.POOL_STRATEGIES_FACTORY_RESOLVER.getKey(), MockConnectionAcquiringStrategyFactoryResolver.class.getName());
            properties.put(PropertyLoader.PropertyKey.POOL_EVENT_LISTENER_RESOLVER.getKey(), MockEventListenerResolver.class.getName());
            properties.put(PropertyLoader.PropertyKey.POOL_TIME_THRESHOLD_CONNECTION_ACQUIRE.getKey(), "120");
            properties.put(PropertyLoader.PropertyKey.POOL_TIME_THRESHOLD_CONNECTION_LEASE.getKey(), "240");
            PropertiesTestUtils.setProperties(properties);
            PropertyLoader propertyLoader = new PropertyLoader();
            assertNotNull(propertyLoader.getDataSource());
            MockDataSource mockDataSource = propertyLoader.getDataSource();
            assertEquals("sa", mockDataSource.getUser());
            assertEquals("admin", mockDataSource.getPassword());
            assertEquals("jdbc://host:1234/database", mockDataSource.getUrl());
            assertNotNull(propertyLoader.getDataSource());
            assertNotNull(propertyLoader.getDataSource());
            assertNotNull(propertyLoader.getMetricsFactory());
            assertNotNull(propertyLoader.getConnectionProxyFactory());
            assertEquals(123, propertyLoader.getMetricLogReporterMillis().intValue());
            assertTrue(propertyLoader.isJmxAutoStart());
            assertFalse(propertyLoader.isJmxEnabled());
            assertNotNull(propertyLoader.getPoolAdapterFactory());
            assertEquals(1, propertyLoader.getConnectionAcquiringStrategyFactories().size());
            assertEquals(MockEventListenerResolver.MockEventListener.class, propertyLoader.getEventListenerResolver().resolveListeners().get(0).getClass());
            assertEquals(Long.valueOf(120), propertyLoader.getConnectionAcquireTimeThresholdMillis());
            assertEquals(Long.valueOf(240), propertyLoader.getConnectionLeaseTimeThresholdMillis());
        } catch (IOException e) {
            fail("Can't save/load properties");
        }
    }

    @Test
    public void testLoadPropertiesWithLazyJndi() {
        testDataSourceJndiLookup(true);
    }

    @Test
    public void testLoadPropertiesWithoutLazyJndi() {
        testDataSourceJndiLookup(false);
    }

    private void testDataSourceJndiLookup(boolean lazy) {
        try {
            DataSource mockDataSource = new MockDataSource();
            JndiTestUtils jndiTestUtils = new JndiTestUtils();
            jndiTestUtils.namingContext().bind("jdbc/DS", mockDataSource);
            Properties properties = new Properties();
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey(), "jdbc/DS");
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_JNDI_NAME.getKey(), "jdbc/DS");
            properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_JNDI_LAZY_LOOKUP.getKey(), String.valueOf(lazy));
            PropertiesTestUtils.setProperties(properties);
            PropertyLoader propertyLoader = new PropertyLoader();
            assertEquals(lazy, Proxy.isProxyClass(propertyLoader.getDataSource().getClass()));
        } catch (IOException e) {
            fail("Can't save/load properties");
        }
    }

    public static class MockPoolAdapterFactory implements PoolAdapterFactory {

        @Override
        public PoolAdapter newInstance(ConfigurationProperties configurationProperties) {
            return null;
        }
    }

    public static class MockMetricsFactory implements MetricsFactory {

        @Override
        public Metrics newInstance(ConfigurationProperties configurationProperties) {
            return null;
        }
    }

    public static class MockConnectionProxyFactory extends ConnectionProxyFactory {

        @Override
        protected Connection proxyConnection(Connection target, ConnectionCallback callback) {
            return null;
        }
    }

    public static class MockConnectionAcquiringStrategyFactoryResolver implements ConnectionAcquiringStrategyFactoryResolver {

        private static class MockConnectionAcquiringStrategyFactory implements ConnectionAcquiringStrategyFactory {

            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return null;
            }
        }

        @Override
        public List<ConnectionAcquiringStrategyFactory> resolveFactories() {
            List<ConnectionAcquiringStrategyFactory> factories = new ArrayList<ConnectionAcquiringStrategyFactory>();
            factories.add(new MockConnectionAcquiringStrategyFactory());
            return factories;
        }
    }

    public static class MockEventListenerResolver implements EventListenerResolver {

        private static class MockEventListener extends EventListener<ConnectionAcquireTimeoutEvent> {

            protected MockEventListener() {
                super(ConnectionAcquireTimeoutEvent.class);
            }

            @Override
            public void on(ConnectionAcquireTimeoutEvent event) {

            }
        }

        @Override
        public List<EventListener<? extends Event>> resolveListeners() {
            List<EventListener<? extends Event>> listeners = new ArrayList<EventListener<? extends Event>>();
            listeners.add(new MockEventListener());
            return listeners;
        }
    }
}
package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.event.EventListenerResolver;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactoryResolver;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import com.vladmihalcea.flexypool.util.JndiUtils;
import com.vladmihalcea.flexypool.util.LazyJndiResolver;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <code>PropertyLoader</code> - The Property Loader allows declarative configuration through the <code>flexy-pool.properties</code> file.
 * It loads the {@link Properties} configuration file and it's then used to create a {@link Configuration} object.
 *
 * @author Vlad Mihalcea
 * @since 1.2
 */
public class PropertyLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLoader.class);

    public static final String PROPERTIES_FILE_PATH = "flexy.pool.properties.path";
    public static final String PROPERTIES_FILE_NAME = "flexy-pool.properties";

    /**
     * Each Property has a well-defined key.
     */
    public enum PropertyKey {
        DATA_SOURCE_UNIQUE_NAME("flexy.pool.data.source.unique.name"),
        DATA_SOURCE_JNDI_NAME("flexy.pool.data.source.jndi.name"),
        DATA_SOURCE_JNDI_LAZY_LOOKUP("flexy.pool.data.source.jndi.lazy.lookup"),
        DATA_SOURCE_CLASS_NAME("flexy.pool.data.source.class.name"),
        DATA_SOURCE_PROPERTY("flexy.pool.data.source.property."),
        POOL_ADAPTER_FACTORY("flexy.pool.adapter.factory"),
        POOL_METRICS_FACTORY("flexy.pool.metrics.factory"),
        POOL_CONNECTION_PROXY_FACTORY("flexy.pool.connection.proxy.factory"),
        POOL_METRICS_REPORTER_LOG_MILLIS("flexy.pool.metrics.reporter.log.millis"),
        POOL_METRICS_REPORTER_JMX_ENABLE("flexy.pool.metrics.reporter.jmx.enable"),
        POOL_METRICS_REPORTER_JMX_AUTO_START("flexy.pool.metrics.reporter.jmx.auto.start"),
        POOL_STRATEGIES_FACTORY_RESOLVER("flexy.pool.strategies.factory.resolver"),
        POOL_EVENT_LISTENER_RESOLVER("flexy.pool.event.listener.resolver"),
        POOL_TIME_THRESHOLD_CONNECTION_ACQUIRE("flexy.pool.time.threshold.connection.acquire"),
        POOL_TIME_THRESHOLD_CONNECTION_LEASE("flexy.pool.time.threshold.connection.lease"),;

        private final String key;

        PropertyKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private final Properties properties = new Properties();

    public PropertyLoader() {
        load();
    }

    public PropertyLoader(Properties overridingProperties) {
        this();
        properties.putAll( overridingProperties );
    }

    /**
     * Load {@link Properties} from the resolved {@link InputStream}
     */
    private void load() {
        InputStream propertiesInputStream = null;
        try {
            propertiesInputStream = propertiesInputStream();
            if (propertiesInputStream == null) {
                throw new IllegalArgumentException("The properties file could not be loaded!");
            }
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            LOGGER.error("Can't load properties", e);
        } finally {
            try {
                if (propertiesInputStream != null) {
                    propertiesInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("Can't close the properties InputStream", e);
            }
        }
    }

    /**
     * Get {@link Properties} file {@link InputStream}
     *
     * @return {@link Properties} file {@link InputStream}
     * @throws IOException the file couldn't be loaded properly
     */
    private InputStream propertiesInputStream() throws IOException {
        String propertiesFilePath = System.getProperty(PROPERTIES_FILE_PATH);
        URL propertiesFileUrl = null;
        if (propertiesFilePath != null) {
            try {
                propertiesFileUrl = new URL(propertiesFilePath);
            } catch (MalformedURLException ignore) {
                propertiesFileUrl = ClassLoaderUtils.getResource(propertiesFilePath);
                if (propertiesFileUrl == null) {
                    File f = new File(propertiesFilePath);
                    if (f.exists() && f.isFile()) {
                        try {
                            propertiesFileUrl = f.toURI().toURL();
                        } catch (MalformedURLException e) {
                            LOGGER.error("The property " + propertiesFilePath + " can't be resolved to either a URL/a Classpath resource or a File");
                        }
                    }
                }
            }
            if (propertiesFileUrl != null) {
                return propertiesFileUrl.openStream();
            }
        }
        return ClassLoaderUtils.getResourceAsStream(PROPERTIES_FILE_NAME);
    }

    /**
     * Get {@link DataSource} unique name
     *
     * @return {@link DataSource} unique name
     */
    public String getUniqueName() {
        return properties.getProperty(PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey());
    }

    /**
     * Get associated {@link DataSource}. The {@link DataSource} can either be looked up in JNDI or instantiated
     * from the configuration meta-data.
     *
     * @return {@link DataSource}
     */
    public <T extends DataSource> T getDataSource() {
        T dataSource = jndiLookup(PropertyKey.DATA_SOURCE_JNDI_NAME);
        if (dataSource != null) {
            return dataSource;
        }
        dataSource = instantiateClass(PropertyKey.DATA_SOURCE_CLASS_NAME);
        if (dataSource == null) {
            throw new IllegalArgumentException("The " + PropertyKey.DATA_SOURCE_CLASS_NAME + " property is mandatory!");
        }
        return applyDataSourceProperties(dataSource);
    }

    /**
     * Apply DataSource specific properties
     */
    private <T extends DataSource> T applyDataSourceProperties(T dataSource) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            String propertyKey = PropertyKey.DATA_SOURCE_PROPERTY.getKey();
            if (key.startsWith(propertyKey)) {
                String dataSourceProperty = key.substring(propertyKey.length());
                ReflectionUtils.invokeSetter(dataSource, dataSourceProperty, value);
            }
        }
        return dataSource;
    }

    /**
     * Get the {@link PoolAdapterFactory}
     *
     * @return {@link PoolAdapterFactory}
     */
    public <T extends DataSource> PoolAdapterFactory<T> getPoolAdapterFactory() {
        return instantiateClass(PropertyKey.POOL_ADAPTER_FACTORY);
    }

    /**
     * Get the {@link MetricsFactory}
     *
     * @return {@link MetricsFactory}
     */
    public MetricsFactory getMetricsFactory() {
        return instantiateClass(PropertyKey.POOL_METRICS_FACTORY);
    }

    /**
     * Get the {@link ConnectionProxyFactory}
     *
     * @return {@link ConnectionProxyFactory}
     */
    public ConnectionProxyFactory getConnectionProxyFactory() {
        return instantiateClass(PropertyKey.POOL_CONNECTION_PROXY_FACTORY);
    }

    /**
     * Get log reporter millis
     *
     * @return log reporter millis
     */
    public Integer getMetricLogReporterMillis() {
        return integerProperty(PropertyKey.POOL_METRICS_REPORTER_LOG_MILLIS);
    }

    /**
     * Is JMX Reporter enabled
     *
     * @return JMX Reporter enabled
     */
    public Boolean isJmxEnabled() {
        return booleanProperty(PropertyKey.POOL_METRICS_REPORTER_JMX_ENABLE);
    }

    /**
     * Is JMX Reporter auto-started
     *
     * @return JMX Reporter auto-started
     */
    public Boolean isJmxAutoStart() {
        return booleanProperty(PropertyKey.POOL_METRICS_REPORTER_JMX_AUTO_START);
    }

    /**
     * Is JNDI lazy lookup
     *
     * @return JNDI lazy lookup
     */
    public boolean isJndiLazyLookup() {
        return Boolean.TRUE.equals(booleanProperty(PropertyKey.DATA_SOURCE_JNDI_LAZY_LOOKUP));
    }

    /**
     * Get the array of {@link ConnectionAcquiringStrategyFactory} for this {@link com.vladmihalcea.flexypool.FlexyPoolDataSource}
     *
     * @return the array of {@link ConnectionAcquiringStrategyFactory}
     */
    @SuppressWarnings("unchecked")
    public <T extends DataSource> List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>> getConnectionAcquiringStrategyFactories() {
        ConnectionAcquiringStrategyFactoryResolver<T> connectionAcquiringStrategyFactoryResolver =
                instantiateClass(PropertyKey.POOL_STRATEGIES_FACTORY_RESOLVER);
        if (connectionAcquiringStrategyFactoryResolver != null) {
            return connectionAcquiringStrategyFactoryResolver.resolveFactories();

        }
        return Collections.emptyList();
    }

    /**
     * Get the event listener resolver
     *
     * @return event listener resolver
     */
    public EventListenerResolver getEventListenerResolver() {
        return instantiateClass(PropertyKey.POOL_EVENT_LISTENER_RESOLVER);
    }

    /**
     * Get the connection acquire time threshold millis
     *
     * @return connection acquire time threshold millis
     */
    public Long getConnectionAcquireTimeThresholdMillis() {
        return longProperty(PropertyKey.POOL_TIME_THRESHOLD_CONNECTION_ACQUIRE);
    }

    /**
     * Get the connection lease time threshold millis
     *
     * @return connection lease time threshold millis
     */
    public Long getConnectionLeaseTimeThresholdMillis() {
        return longProperty(PropertyKey.POOL_TIME_THRESHOLD_CONNECTION_LEASE);
    }

    /**
     * Instantiate class associated to the given property key
     *
     * @param propertyKey property key
     * @param <T>         class parameter type
     * @return class instance
     */
    private <T> T instantiateClass(PropertyKey propertyKey) {
        T object = null;
        String property = properties.getProperty(propertyKey.getKey());
        if (property != null) {
            try {
                Class<T> clazz = ClassLoaderUtils.loadClass(property);
                LOGGER.debug("Instantiate {}", clazz);
                object = clazz.newInstance();
            } catch (ClassNotFoundException e) {
                LOGGER.error("Couldn't load the " + property + " class given by the " + propertyKey + " property", e);
            } catch (InstantiationException e) {
                LOGGER.error("Couldn't instantiate the " + property + " class given by the " + propertyKey + " property", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Couldn't access the " + property + " class given by the " + propertyKey + " property", e);
            }
        }
        return object;
    }

    /**
     * Get Integer property value
     *
     * @param propertyKey property key
     * @return Integer property value
     */
    private Integer integerProperty(PropertyKey propertyKey) {
        Integer value = null;
        String property = properties.getProperty(propertyKey.getKey());
        if (property != null) {
            value = Integer.valueOf(property);
        }
        return value;
    }

    /**
     * Get Long property value
     *
     * @param propertyKey property key
     * @return Long property value
     */
    private Long longProperty(PropertyKey propertyKey) {
        Long value = null;
        String property = properties.getProperty(propertyKey.getKey());
        if (property != null) {
            value = Long.valueOf(property);
        }
        return value;
    }

    /**
     * Get Boolean property value
     *
     * @param propertyKey property key
     * @return Boolean property value
     */
    private Boolean booleanProperty(PropertyKey propertyKey) {
        Boolean value = null;
        String property = properties.getProperty(propertyKey.getKey());
        if (property != null) {
            value = Boolean.valueOf(property);
        }
        return value;
    }

    /**
     * Lookup object from JNDI
     *
     * @param propertyKey property key
     * @param <T>         JNDI object type
     * @return JNDI object
     */
    @SuppressWarnings("unchecked")
    private <T> T jndiLookup(PropertyKey propertyKey) {
        String property = properties.getProperty(propertyKey.getKey());
        if (property != null) {
            return isJndiLazyLookup() ?
                    (T) LazyJndiResolver.newInstance(property, DataSource.class) :
                    (T) JndiUtils.lookup(property);
        }
        return null;
    }
}

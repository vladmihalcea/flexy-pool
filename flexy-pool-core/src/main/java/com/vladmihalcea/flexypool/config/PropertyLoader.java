package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactoryResolver;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import com.vladmihalcea.flexypool.util.JndiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * PropertyLoader - The Property Loader scans the class-path for a flexy-pool.properties file and loads the
 * available configuration properties.
 *
 * @author Vlad Mihalcea
 */
public class PropertyLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLoader.class);

    public static final String PROPERTIES_FILE_NAME = "flexy-pool.properties";

    public enum PropertyKey {
        DATA_SOURCE_UNIQUE_NAME("flexy.pool.data.source.unique.name"),
        DATA_SOURCE_CLASS_NAME("flexy.pool.data.source.class.name"),
        DATA_SOURCE_PROPERTY("flexy.pool.data.source.property."),
        POOL_ADAPTER_FACTORY("flexy.pool.adapter.factory"),
        POOL_METRICS_FACTORY("flexy.pool.metrics.factory"),
        POOL_STRATEGIES_FACTORY_RESOLVER("flexy.pool.strategies.factory.resolver");

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

    /**
     * Load Properties from class-path configuration file.
     */
    private void load() {
        InputStream propertiesInputStream = ClassLoaderUtils.getResourceAsStream(PROPERTIES_FILE_NAME);
        if (propertiesInputStream != null) {
            try {
                properties.load(propertiesInputStream);
            } catch (IOException e) {
                LOGGER.error("Can't load properties", e);
            } finally {
                try {
                    propertiesInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Can't close the properties InputStream", e);
                }
            }
        }
    }

    /**
     * Get DataSource unique name
     *
     * @return DataSource unique name
     */
    public String getUniqueName() {
        return properties.getProperty(PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey());
    }

    /**
     * Get DataSource from JNDI
     *
     * @return DataSource
     */
    public <T extends DataSource> T getDataSource() {
        T dataSource = instantiateClass(PropertyKey.DATA_SOURCE_CLASS_NAME);
        if(dataSource == null) {
            throw new IllegalArgumentException("The " +  PropertyKey.DATA_SOURCE_CLASS_NAME+ " property is mandatory!");
        }
        return applyDataSourceProperties(dataSource);
    }

    /**
     * Apply DataSource specific properties
     */
    private <T extends DataSource> T applyDataSourceProperties(T dataSource) {
        Class<?> dataSourceClass = dataSource.getClass();

        for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            String propertyKey = PropertyKey.DATA_SOURCE_PROPERTY.getKey();
            if(key.startsWith(propertyKey)) {
                String dataSourceProperty = key.substring(propertyKey.length());
                try {
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(dataSourceProperty, dataSourceClass);
                    propertyDescriptor.getWriteMethod().invoke(dataSource, value);
                } catch (IntrospectionException e) {
                    LOGGER.error("Can't find " + dataSourceProperty + " property in " + dataSourceClass, e);
                } catch (InvocationTargetException e) {
                    LOGGER.error("Can't set  " + dataSourceProperty + " property in " + dataSourceClass, e);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Can't access  " + dataSourceProperty + " property in " + dataSourceClass, e);
                }
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
     * Get the array of {@link ConnectionAcquiringStrategyFactory} for this {@link FlexyPoolDataSource}
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
     * Instantiate class associated to teh given proeprty key
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
     * Lookup object from JNDI
     *
     * @param propertyKey property key
     * @param <T>         JNDI object type
     * @return JNDI object
     */
    @SuppressWarnings("unchecked")
    private <T> T jndiLookup(PropertyKey propertyKey) {
        String property = properties.getProperty(propertyKey.getKey());
        if(property == null) {
            throw new IllegalArgumentException("The " + propertyKey + " property is mandatory!");
        }
        return (T) JndiUtils.lookup(property);
    }
}

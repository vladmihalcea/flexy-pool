package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactory;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactoryResolver;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.jpa.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * FlexyPoolConnectionProvider - This is the Hibernate custom DataSource adapter.
 * It allows registering the {@link com.vladmihalcea.flexypool.FlexyPoolDataSource} instead of the original
 * {@link javax.sql.DataSource} defined in the persistence.xml descriptor.
 *
 * @author Vlad Mihalcea
 */
public class FlexyPoolHibernateConnectionProvider extends DatasourceConnectionProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexyPoolHibernateConnectionProvider.class);

    public interface Properties {
        String POOL_ADAPTER_FACTORY = "flexy.pool.adapter.factory";
        String POOL_METRICS_FACTORY = "flexy.pool.metrics.factory";
        String POOL_STRATEGIES_FACTORY_RESOLVER = "flexy.pool.strategies.factory.resolver";
    }

    private FlexyPoolDataSource<DataSource> flexyPoolDataSource;

    @Override
    public void configure(Map props) throws HibernateException {
        super.configure(props);
        LOGGER.debug("Configuring FlexyPool DataSource");
        flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(
                getFlexyPoolConfiguration(props),
                getConnectionAcquiringStrategyFactories(props));
        LOGGER.debug("FlexyPool DataSource Configured");
    }

    /**
     * Get the associated {@link FlexyPoolDataSource} {@link Configuration}
     *
     * @param props JPA/Hibernate properties
     * @return {@link Configuration}
     */
    protected Configuration<DataSource> getFlexyPoolConfiguration(Map props) {
        MetricsFactory metricsFactory = getMetricsFactory(props);
        Configuration.Builder<DataSource> configurationBuilder = new Configuration.Builder<DataSource>(
                String.valueOf(props.get(AvailableSettings.PERSISTENCE_UNIT_NAME)),
                getDataSource(),
                getPoolAdapterFactory(props)
        );
        if (metricsFactory != null) {
            configurationBuilder.setMetricsFactory(metricsFactory);
        }
        return configurationBuilder.build();
    }

    /**
     * Get the current {@link PoolAdapterFactory}
     *
     * @param props JPA/Hibernate properties
     * @return current {@link PoolAdapterFactory}
     */
    protected PoolAdapterFactory<DataSource> getPoolAdapterFactory(Map props) {
        PoolAdapterFactory<DataSource> poolAdapterFactory = DataSourcePoolAdapter.FACTORY;
        Object poolAdapterFactoryProperty = props.get(Properties.POOL_ADAPTER_FACTORY);
        if (poolAdapterFactoryProperty != null) {
            try {
                Class<PoolAdapterFactory<DataSource>> poolAdapterFactoryClass = ClassLoaderUtils.loadClass(poolAdapterFactoryProperty.toString());
                poolAdapterFactory = poolAdapterFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                LOGGER.error("Couldn't load the " + poolAdapterFactoryProperty + " class given by the " + Properties.POOL_ADAPTER_FACTORY + " property", e);
            } catch (InstantiationException e) {
                LOGGER.error("Couldn't instantiate the " + poolAdapterFactoryProperty + " class given by the " + Properties.POOL_ADAPTER_FACTORY + " property", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Couldn't access the " + poolAdapterFactoryProperty + " class given by the " + Properties.POOL_ADAPTER_FACTORY + " property", e);
            }
        }
        LOGGER.debug("Using {} PoolAdapterFactory", poolAdapterFactory);
        return poolAdapterFactory;
    }

    /**
     * Get the current {@link MetricsFactory}
     *
     * @param props JPA/Hibernate properties
     * @return current {@link MetricsFactory}
     */
    protected MetricsFactory getMetricsFactory(Map props) {
        Object metricsFactoryProperty = props.get(Properties.POOL_METRICS_FACTORY);
        if (metricsFactoryProperty != null) {
            try {
                Class<MetricsFactory> metricsFactoryClass = ClassLoaderUtils.loadClass(metricsFactoryProperty.toString());
                return metricsFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                LOGGER.error("Couldn't load the " + metricsFactoryProperty + " class given by the " + Properties.POOL_METRICS_FACTORY + " property", e);
            } catch (InstantiationException e) {
                LOGGER.error("Couldn't instantiate the " + metricsFactoryProperty + " class given by the " + Properties.POOL_METRICS_FACTORY + " property", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Couldn't access the " + metricsFactoryProperty + " class given by the " + Properties.POOL_METRICS_FACTORY + " property", e);
            }
        }
        return null;
    }

    /**
     * Get an array of {@link ConnectionAcquiringStrategyFactory} for this {@link FlexyPoolDataSource}
     * @param props JPA/Hibernate properties
     * @return the array of {@link ConnectionAcquiringStrategyFactory}
     */
    @SuppressWarnings("unchecked")
    private ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, DataSource>[]
            getConnectionAcquiringStrategyFactories(Map props) {
        ConnectionAcquiringStrategyFactoryResolver<DataSource> connectionAcquiringStrategyFactoryResolver =
                getConnectionAcquiringStrategyFactoryResolver(props);
        if (connectionAcquiringStrategyFactoryResolver != null) {
            return (ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, DataSource>[])
                connectionAcquiringStrategyFactoryResolver.resolveFactories().toArray();
        }
        return new ConnectionAcquiringStrategyFactory[]{};
    }

    /**
     * Get the current {@link ConnectionAcquiringStrategyFactoryResolver}
     *
     * @param props JPA/Hibernate properties
     * @return current {@link ConnectionAcquiringStrategyFactoryResolver}
     */
    @SuppressWarnings("unchecked")
    protected ConnectionAcquiringStrategyFactoryResolver<DataSource> getConnectionAcquiringStrategyFactoryResolver(Map props) {
        Object poolStrategyFactoryResolverProperty = props.get(Properties.POOL_STRATEGIES_FACTORY_RESOLVER);
        if (poolStrategyFactoryResolverProperty != null) {
            try {
                Class<ConnectionAcquiringStrategyFactoryResolver> metricsFactoryClass =
                        ClassLoaderUtils.loadClass(poolStrategyFactoryResolverProperty.toString());
                return metricsFactoryClass.newInstance();
            } catch (ClassNotFoundException e) {
                LOGGER.error("Couldn't load the " + poolStrategyFactoryResolverProperty + " class given by the " + Properties.POOL_STRATEGIES_FACTORY_RESOLVER + " property", e);
            } catch (InstantiationException e) {
                LOGGER.error("Couldn't instantiate the " + poolStrategyFactoryResolverProperty + " class given by the " + Properties.POOL_STRATEGIES_FACTORY_RESOLVER + " property", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Couldn't access the " + poolStrategyFactoryResolverProperty + " class given by the " + Properties.POOL_STRATEGIES_FACTORY_RESOLVER + " property", e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return this.flexyPoolDataSource.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean isUnwrappableAs(Class unwrapType) {
        return super.isUnwrappableAs(unwrapType) || FlexyPoolHibernateConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.flexyPoolDataSource.stop();
        super.stop();
    }
}

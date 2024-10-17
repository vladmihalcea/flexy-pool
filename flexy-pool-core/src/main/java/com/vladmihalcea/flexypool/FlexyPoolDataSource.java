package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.adaptor.DataSourcePoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.config.FlexyPoolConfiguration;
import com.vladmihalcea.flexypool.config.PropertyLoader;
import com.vladmihalcea.flexypool.connection.ConnectionPoolCallback;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.event.ConnectionAcquisitionTimeThresholdExceededEvent;
import com.vladmihalcea.flexypool.event.ConnectionLeaseTimeThresholdExceededEvent;
import com.vladmihalcea.flexypool.event.EventListenerResolver;
import com.vladmihalcea.flexypool.event.EventPublisher;
import com.vladmihalcea.flexypool.exception.ConnectionAcquisitionTimeoutException;
import com.vladmihalcea.flexypool.exception.ConnectionAcquisitionException;
import com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquisitionStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquisitionStrategyFactory;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * <code>FlexyPoolDataSource</code> is a {@link DataSource} wrapper that allows multiple
 * {@link ConnectionAcquisitionStrategy} to be applied when trying to acquireConnection a database {@link java.sql.Connection}.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class FlexyPoolDataSource<T extends DataSource> implements DataSource, LifeCycleCallback, ConnectionPoolCallback, Closeable {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FlexyPoolDataSource.class);

    private static class FlexyPoolDataSourceConfiguration<D extends DataSource> {
        private final FlexyPoolConfiguration<D> configuration;

        private final List<ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, D>>
                connectionAcquiringStrategyFactories;

        public FlexyPoolDataSourceConfiguration(
                FlexyPoolConfiguration<D> configuration,
                List<ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, D>> connectionAcquiringStrategyFactories) {
            this.configuration = configuration;
            this.connectionAcquiringStrategyFactories = connectionAcquiringStrategyFactories;
        }

        public FlexyPoolConfiguration<D> getConfiguration() {
            return configuration;
        }

        public List<ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, D>> getConnectionAcquiringStrategyFactories() {
            return connectionAcquiringStrategyFactories;
        }
    }

    private static class ConfigurationLoader<D extends DataSource> {

        private final PropertyLoader propertyLoader;

        private final FlexyPoolDataSourceConfiguration<D> flexyPoolDataSourceConfiguration;

        @SuppressWarnings("unchecked")
        public ConfigurationLoader() {
            this.propertyLoader = new PropertyLoader();
            D dataSource = propertyLoader.getDataSource();
            flexyPoolDataSourceConfiguration = init(dataSource);
        }

        public ConfigurationLoader(D dataSource) {
            this.propertyLoader = new PropertyLoader();
            flexyPoolDataSourceConfiguration = init(dataSource);
        }

        public ConfigurationLoader(D dataSource, Properties overridingProperties) {
            this.propertyLoader = new PropertyLoader(overridingProperties);
            flexyPoolDataSourceConfiguration = init(dataSource);
        }

        private FlexyPoolDataSourceConfiguration<D> init(D dataSource) {
            return new FlexyPoolDataSourceConfiguration<D>(
                    configuration(dataSource),
                    connectionAcquiringStrategyFactories()
            );
        }

        @SuppressWarnings("unchecked")
        private FlexyPoolConfiguration<D> configuration(D dataSource) {
            String uniqueName = propertyLoader.getUniqueName();
            PoolAdapterFactory<D> poolAdapterFactory = propertyLoader.getPoolAdapterFactory();
            MetricsFactory metricsFactory = propertyLoader.getMetricsFactory();
            ConnectionProxyFactory connectionProxyFactory = propertyLoader.getConnectionProxyFactory();
            Integer metricLogReporterMillis = propertyLoader.getMetricLogReporterMillis();
            Boolean jmxEnabled = propertyLoader.isJmxEnabled();
            Boolean jmxAutoStart = propertyLoader.isJmxAutoStart();
            EventListenerResolver eventListenerResolver = propertyLoader.getEventListenerResolver();
            Long connectionAcquisitionTimeThresholdMillis = propertyLoader.getConnectionAcquisitionTimeThresholdMillis();
            Long connectionLeaseTimeThresholdMillis = propertyLoader.getConnectionLeaseTimeThresholdMillis();

            if (poolAdapterFactory == null) {
                poolAdapterFactory = (PoolAdapterFactory<D>) DataSourcePoolAdapter.FACTORY;
            }

            FlexyPoolConfiguration.Builder<D> configurationBuilder = new FlexyPoolConfiguration.Builder<D>(
                    uniqueName, dataSource, poolAdapterFactory
            );
            if (metricsFactory != null) {
                configurationBuilder.setMetricsFactory(metricsFactory);
            }
            if (connectionProxyFactory != null) {
                configurationBuilder.setConnectionProxyFactory(connectionProxyFactory);
            }
            if (metricLogReporterMillis != null) {
                configurationBuilder.setMetricLogReporterMillis(metricLogReporterMillis);
            }
            if (jmxEnabled != null) {
                configurationBuilder.setJmxEnabled(jmxEnabled);
            }
            if (jmxAutoStart != null) {
                configurationBuilder.setJmxAutoStart(jmxAutoStart);
            }
            if (eventListenerResolver != null) {
                configurationBuilder.setEventListenerResolver(eventListenerResolver);
            }
            if (connectionAcquisitionTimeThresholdMillis != null) {
                configurationBuilder.setConnectionAcquisitionTimeThresholdMillis( connectionAcquisitionTimeThresholdMillis);
            }
            if (connectionLeaseTimeThresholdMillis != null) {
                configurationBuilder.setConnectionLeaseTimeThresholdMillis(connectionLeaseTimeThresholdMillis);
            }
            return configurationBuilder.build();
        }

        private List<ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, D>>
        connectionAcquiringStrategyFactories() {
            return propertyLoader.getConnectionAcquiringStrategyFactories();
        }

        public FlexyPoolDataSourceConfiguration<D> getFlexyPoolDataSourceConfiguration() {
            return flexyPoolDataSourceConfiguration;
        }
    }

    public static final String OVERALL_CONNECTION_ACQUISITION_MILLIS = "overallConnectionAcquisitionMillis";
    public static final String CONCURRENT_CONNECTIONS_HISTOGRAM = "concurrentConnectionsHistogram";
    public static final String CONCURRENT_CONNECTION_REQUESTS_HISTOGRAM = "concurrentConnectionRequestsHistogram";
    public static final String CONNECTION_LEASE_MILLIS = "connectionLeaseMillis";

    private final String uniqueName;
    private final PoolAdapter<T> poolAdapter;
    private final T targetDataSource;
    private final Metrics metrics;
    private final Timer connectionAcquisitionTotalTimer;
    private final Histogram concurrentConnectionCountHistogram;
    private final Histogram concurrentConnectionRequestCountHistogram;
    private final Timer connectionLeaseTimer;
    private final ConnectionProxyFactory connectionProxyFactory;
    private final Collection<ConnectionAcquisitionStrategy> connectionAcquiringStrategies =
            new LinkedHashSet<ConnectionAcquisitionStrategy>();

    private AtomicLong concurrentConnectionCount = new AtomicLong();
    private AtomicLong concurrentConnectionRequestCount = new AtomicLong();

    private final EventPublisher eventPublisher;

    private final long connectionAcquisitionTimeThresholdMillis;
    private final long connectionLeaseTimeThresholdMillis;

    /**
     * Initialize <code>FlexyPoolDataSource</code> from {@link FlexyPoolConfiguration} and the array of {@link ConnectionAcquisitionStrategyFactory}
     *
     * @param configuration                        configuration
     * @param connectionAcquiringStrategyFactories array of {@link ConnectionAcquisitionStrategyFactory}
     */
    public FlexyPoolDataSource(final FlexyPoolConfiguration<T> configuration,
                               ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, T>... connectionAcquiringStrategyFactories) {
        this(configuration, Arrays.asList(connectionAcquiringStrategyFactories));
    }

    /**
     * Initialize <code>FlexyPoolDataSource</code> from declarative properties configuration.
     */
    public FlexyPoolDataSource() {
        this(new ConfigurationLoader<T>().getFlexyPoolDataSourceConfiguration());
    }

    /**
     * Initialize <code>FlexyPoolDataSource</code> from declarative properties configuration and using the given
     * target {@link DataSource}
     *
     * @param targetDataSource target {@link DataSource}
     */
    public FlexyPoolDataSource(T targetDataSource) {
        this(new ConfigurationLoader<T>(targetDataSource).getFlexyPoolDataSourceConfiguration());
    }

    /**
     * Initialize <code>FlexyPoolDataSource</code> from declarative properties configuration and using the given
     * target {@link DataSource}
     *
     * @param targetDataSource target {@link DataSource}
     * @param overridingProperties overriding properties {@link Properties}
     */
    public FlexyPoolDataSource(T targetDataSource, Properties overridingProperties) {
        this(new ConfigurationLoader<T>(targetDataSource, overridingProperties).getFlexyPoolDataSourceConfiguration());
    }

    /**
     * Initialize <code>FlexyPoolDataSource</code> from {@link FlexyPoolConfiguration} and the associated list of {@link ConnectionAcquisitionStrategyFactory}
     *
     * @param configuration                        configuration
     * @param connectionAcquiringStrategyFactories list of {@link ConnectionAcquisitionStrategyFactory}
     */
    private FlexyPoolDataSource(final FlexyPoolConfiguration<T> configuration,
                                List<ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, T>> connectionAcquiringStrategyFactories) {
        this.uniqueName = configuration.getUniqueName();
        this.poolAdapter = configuration.getPoolAdapter();
        this.targetDataSource = poolAdapter.getTargetDataSource();
        this.metrics = configuration.getMetrics();
        this.connectionAcquisitionTotalTimer = metrics.timer( OVERALL_CONNECTION_ACQUISITION_MILLIS );
        this.concurrentConnectionCountHistogram = metrics.histogram(CONCURRENT_CONNECTIONS_HISTOGRAM);
        this.concurrentConnectionRequestCountHistogram = metrics.histogram(CONCURRENT_CONNECTION_REQUESTS_HISTOGRAM);
        this.connectionLeaseTimer = metrics.timer(CONNECTION_LEASE_MILLIS);
        this.connectionProxyFactory = configuration.getConnectionProxyFactory();
        if (connectionAcquiringStrategyFactories.isEmpty()) {
            LOGGER.info("FlexyPool is not using any strategy!");
        }
        for ( ConnectionAcquisitionStrategyFactory<? extends ConnectionAcquisitionStrategy, T>
                connectionAcquiringStrategyFactory : connectionAcquiringStrategyFactories) {
            connectionAcquiringStrategies.add(connectionAcquiringStrategyFactory.newInstance(configuration));
        }
        eventPublisher = configuration.getEventPublisher();
        connectionAcquisitionTimeThresholdMillis = configuration.getConnectionAcquisitionTimeThresholdMillis();
        connectionLeaseTimeThresholdMillis = configuration.getConnectionLeaseTimeThresholdMillis();
    }

    /**
     * Initialize <code>FlexyPoolDataSource</code> from {@link FlexyPoolDataSourceConfiguration}
     *
     * @param flexyPoolDataSourceConfiguration configuration
     */
    private FlexyPoolDataSource(FlexyPoolDataSourceConfiguration<T> flexyPoolDataSourceConfiguration) {
        this(flexyPoolDataSourceConfiguration.getConfiguration(),
                flexyPoolDataSourceConfiguration.getConnectionAcquiringStrategyFactories());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(new ConnectionRequestContext.Builder()
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return getConnection(new ConnectionRequestContext.Builder()
                .setCredentials(new Credentials(username, password))
                .build());
    }

    /**
     * Get target DataSource.
     *
     * @return target DataSource
     */
    public T getTargetDataSource() {
        return targetDataSource;
    }

    /**
     * Try to obtain a connection by going through all available strategies
     *
     * @param context context
     * @return connection
     * @throws SQLException
     */
    private Connection getConnection(ConnectionRequestContext context) throws SQLException {
        concurrentConnectionRequestCountHistogram.update(concurrentConnectionRequestCount.incrementAndGet());
        long startNanos = System.nanoTime();
        try {
            Connection connection = null;
            if (!connectionAcquiringStrategies.isEmpty()) {
                for ( ConnectionAcquisitionStrategy strategy : connectionAcquiringStrategies) {
                    try {
                        connection = strategy.getConnection(context);
                        break;
                    } catch (ConnectionAcquisitionTimeoutException e) {
                        LOGGER.warn("Couldn't retrieve connection from strategy {} with context {}", strategy, context);
                    }
                }
            } else {
                connection = poolAdapter.getConnection(context);
            }
            if (connection != null) {
                return connectionProxyFactory.newInstance(connection, this);
            } else {
                throw new ConnectionAcquisitionException( "Couldn't acquire connection for current strategies: " + connectionAcquiringStrategies);
            }
        } finally {
            long endNanos = System.nanoTime();
            long acquisitionDurationMillis = TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos);
            connectionAcquisitionTotalTimer.update( acquisitionDurationMillis, TimeUnit.MILLISECONDS);
            concurrentConnectionRequestCountHistogram.update(concurrentConnectionRequestCount.decrementAndGet());
            if (acquisitionDurationMillis > connectionAcquisitionTimeThresholdMillis ) {
                eventPublisher.publish(new ConnectionAcquisitionTimeThresholdExceededEvent(
                        uniqueName, connectionAcquisitionTimeThresholdMillis, acquisitionDurationMillis
                ));
                LOGGER.info( "Connection acquired in {} millis, while threshold is set to {} in {} FlexyPoolDataSource",
                             acquisitionDurationMillis, connectionAcquisitionTimeThresholdMillis, uniqueName);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acquireConnection() {
        concurrentConnectionCountHistogram.update(concurrentConnectionCount.incrementAndGet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseConnection(long leaseDurationNanos) {
        concurrentConnectionCountHistogram.update(concurrentConnectionCount.decrementAndGet());
        long leaseDurationMillis = TimeUnit.NANOSECONDS.toMillis(leaseDurationNanos);
        connectionLeaseTimer.update(leaseDurationMillis, TimeUnit.MILLISECONDS);
        if (leaseDurationMillis > connectionLeaseTimeThresholdMillis) {
            eventPublisher.publish(new ConnectionLeaseTimeThresholdExceededEvent(
                    uniqueName, connectionLeaseTimeThresholdMillis, leaseDurationMillis
            ));
            LOGGER.info("Connection leased for {} millis, while threshold is set to {} in {} FlexyPoolDataSource",
                    leaseDurationMillis, connectionLeaseTimeThresholdMillis, uniqueName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return targetDataSource.getLogWriter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        targetDataSource.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return targetDataSource.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        targetDataSource.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return targetDataSource.unwrap(iface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return targetDataSource.isWrapperFor(iface);
    }

    /**
     * JDBC 4.1 method, available to work with both java 1.6 and java 1.7
     */
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        metrics.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        metrics.stop();
    }

	@Override
	public void close() throws IOException {
        metrics.stop();
        if (targetDataSource instanceof Closeable) {
        	((Closeable)targetDataSource).close();
        }
	}

}

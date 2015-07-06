package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.adaptor.DataSourcePoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.config.PropertyLoader;
import com.vladmihalcea.flexypool.connection.ConnectionPoolCallback;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.event.EventListenerResolver;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.exception.CantAcquireConnectionException;
import com.vladmihalcea.flexypool.lifecycle.LifeCycleCallback;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactory;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * <code>FlexyPoolDataSource</code> is a {@link DataSource} wrapper that allows multiple
 * {@link ConnectionAcquiringStrategy} to be applied when trying to acquireConnection a database {@link java.sql.Connection}.
 * This is how you'd configure it suing Spring JavaConfig:
 * <p>
 * <pre>
 *
 * {@code @Autowired} private PoolingDataSource poolingDataSource;
 *
 * {@code @Bean} public Configuration configuration() {
 * return new Configuration.Factory<PoolingDataSource>(
 * UUID.randomUUID().toString(),
 * poolingDataSource,
 * BitronixPoolAdapter.FACTORY
 * ).build();
 * }
 *
 * {@code @Bean} public FlexyPoolDataSource dataSource() {
 * Configuration configuration = configuration();
 * return new FlexyPoolDataSource(configuration,
 * new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory(5),
 * new RetryConnectionAcquiringStrategy.Factory(2)
 * );
 * }
 * </pre>
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class FlexyPoolDataSource<T extends DataSource> implements DataSource, LifeCycleCallback, ConnectionPoolCallback {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FlexyPoolDataSource.class);

    private static class FlexyPoolDataSourceConfiguration<D extends DataSource> {
        private final Configuration<D> configuration;

        private final List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, D>>
                connectionAcquiringStrategyFactories;

        public FlexyPoolDataSourceConfiguration(
                Configuration<D> configuration,
                List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, D>> connectionAcquiringStrategyFactories) {
            this.configuration = configuration;
            this.connectionAcquiringStrategyFactories = connectionAcquiringStrategyFactories;
        }

        public Configuration<D> getConfiguration() {
            return configuration;
        }

        public List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, D>> getConnectionAcquiringStrategyFactories() {
            return connectionAcquiringStrategyFactories;
        }
    }

    private static class ConfigurationLoader<D extends DataSource> {

        private final PropertyLoader propertyLoader = new PropertyLoader();

        private final FlexyPoolDataSourceConfiguration<D> flexyPoolDataSourceConfiguration;

        @SuppressWarnings("unchecked")
        public ConfigurationLoader() {
            D dataSource = propertyLoader.getDataSource();
            flexyPoolDataSourceConfiguration = init(dataSource);
        }

        public ConfigurationLoader(D dataSource) {
            flexyPoolDataSourceConfiguration = init(dataSource);
        }

        private FlexyPoolDataSourceConfiguration<D> init(D dataSource) {
            return new FlexyPoolDataSourceConfiguration<D>(
                    configuration(dataSource),
                    connectionAcquiringStrategyFactories()
            );
        }

        @SuppressWarnings("unchecked")
        private Configuration<D> configuration(D dataSource) {
            String uniqueName = propertyLoader.getUniqueName();
            PoolAdapterFactory<D> poolAdapterFactory = propertyLoader.getPoolAdapterFactory();
            MetricsFactory metricsFactory = propertyLoader.getMetricsFactory();
            ConnectionProxyFactory connectionProxyFactory = propertyLoader.getConnectionProxyFactory();
            Integer metricLogReporterMillis = propertyLoader.getMetricLogReporterMillis();
            Boolean jmxEnabled = propertyLoader.isJmxEnabled();
            Boolean jmxAutoStart = propertyLoader.isJmxAutoStart();
            EventListenerResolver eventListenerResolver = propertyLoader.getEventListenerResolver();

            if (poolAdapterFactory == null) {
                poolAdapterFactory = (PoolAdapterFactory<D>) DataSourcePoolAdapter.FACTORY;
            }

            Configuration.Builder<D> configurationBuilder = new Configuration.Builder<D>(
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
            return configurationBuilder.build();
        }

        private List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, D>>
        connectionAcquiringStrategyFactories() {
            return propertyLoader.getConnectionAcquiringStrategyFactories();
        }

        public FlexyPoolDataSourceConfiguration<D> getFlexyPoolDataSourceConfiguration() {
            return flexyPoolDataSourceConfiguration;
        }
    }

    public static final String OVERALL_CONNECTION_ACQUIRE_MILLIS = "overallConnectionAcquireMillis";
    public static final String CONCURRENT_CONNECTIONS_HISTOGRAM = "concurrentConnectionsHistogram";
    public static final String CONCURRENT_CONNECTION_REQUESTS_HISTOGRAM = "concurrentConnectionRequestsHistogram";
    public static final String CONNECTION_LEASE_MILLIS = "connectionLeaseMillis";

    private final PoolAdapter<T> poolAdapter;
    private final T targetDataSource;
    private final Metrics metrics;
    private final Timer connectionAcquireTotalTimer;
    private final Histogram concurrentConnectionCountHistogram;
    private final Histogram concurrentConnectionRequestCountHistogram;
    private final Timer connectionLeaseTimer;
    private final ConnectionProxyFactory connectionProxyFactory;
    private final Collection<ConnectionAcquiringStrategy> connectionAcquiringStrategies =
            new LinkedHashSet<ConnectionAcquiringStrategy>();

    private AtomicLong concurrentConnectionCount = new AtomicLong();
    private AtomicLong concurrentConnectionRequestCount = new AtomicLong();

    /**
     * Initialize <code>FlexyPoolDataSource</code> from {@link Configuration} and the array of {@link ConnectionAcquiringStrategyFactory}
     *
     * @param configuration                        configuration
     * @param connectionAcquiringStrategyFactories array of {@link ConnectionAcquiringStrategyFactory}
     */
    public FlexyPoolDataSource(final Configuration<T> configuration,
                               ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>... connectionAcquiringStrategyFactories) {
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
     * Initialize <code>FlexyPoolDataSource</code> from {@link Configuration} and the associated list of {@link ConnectionAcquiringStrategyFactory}
     *
     * @param configuration                        configuration
     * @param connectionAcquiringStrategyFactories list of {@link ConnectionAcquiringStrategyFactory}
     */
    private FlexyPoolDataSource(final Configuration<T> configuration,
                                List<ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>> connectionAcquiringStrategyFactories) {
        this.poolAdapter = configuration.getPoolAdapter();
        this.targetDataSource = poolAdapter.getTargetDataSource();
        this.metrics = configuration.getMetrics();
        this.connectionAcquireTotalTimer = metrics.timer(OVERALL_CONNECTION_ACQUIRE_MILLIS);
        this.concurrentConnectionCountHistogram = metrics.histogram(CONCURRENT_CONNECTIONS_HISTOGRAM);
        this.concurrentConnectionRequestCountHistogram = metrics.histogram(CONCURRENT_CONNECTION_REQUESTS_HISTOGRAM);
        this.connectionLeaseTimer = metrics.timer(CONNECTION_LEASE_MILLIS);
        this.connectionProxyFactory = configuration.getConnectionProxyFactory();
        if (connectionAcquiringStrategyFactories.isEmpty()) {
            LOGGER.info("FlexyPool is not using any strategy!");
        }
        for (ConnectionAcquiringStrategyFactory<? extends ConnectionAcquiringStrategy, T>
                connectionAcquiringStrategyFactory : connectionAcquiringStrategyFactories) {
            connectionAcquiringStrategies.add(connectionAcquiringStrategyFactory.newInstance(configuration));
        }
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
                for (ConnectionAcquiringStrategy strategy : connectionAcquiringStrategies) {
                    try {
                        connection = strategy.getConnection(context);
                        break;
                    } catch (AcquireTimeoutException e) {
                        LOGGER.warn("Couldn't retrieve connection from strategy {} with context {}", strategy, context);
                    }
                }
            } else {
                connection = poolAdapter.getConnection(context);
            }
            if (connection != null) {
                return connectionProxyFactory.newInstance(connection, this);
            } else {
                throw new CantAcquireConnectionException("Couldn't acquire connection for current strategies: " + connectionAcquiringStrategies);
            }
        } finally {
            long endNanos = System.nanoTime();
            connectionAcquireTotalTimer.update(TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos), TimeUnit.MILLISECONDS);
            concurrentConnectionRequestCountHistogram.update(concurrentConnectionRequestCount.decrementAndGet());
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
        connectionLeaseTimer.update(TimeUnit.NANOSECONDS.toMillis(leaseDurationNanos), TimeUnit.MILLISECONDS);
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

}

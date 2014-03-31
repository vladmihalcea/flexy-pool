package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.connection.ConnectionCallback;
import com.vladmihalcea.flexypool.connection.ConnectionProxyBuilder;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.exception.CantAcquireConnectionException;
import com.vladmihalcea.flexypool.lifecycle.LifeCycleAware;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyBuilder;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * <code>FlexyPoolDataSource</code> is a {@link DataSource} wrapper that allows multiple
 * {@link ConnectionAcquiringStrategy} to be applied when trying to acquire a database {@link java.sql.Connection}.
 * This is how you'd configure it suing Spring JavaConfig:
 * <p/>
 * <pre>
 *
 * {@code @Autowired} private PoolingDataSource poolingDataSource;
 *
 * {@code @Bean} public Configuration configuration() {
 * return new Configuration.Builder<PoolingDataSource>(
 * UUID.randomUUID().toString(),
 * poolingDataSource,
 * CodahaleMetrics.BUILDER,
 * BitronixPoolAdapter.BUILDER
 * ).build();
 * }
 *
 * {@code @Bean} public FlexyPoolDataSource dataSource() {
 * Configuration configuration = configuration();
 * return new FlexyPoolDataSource(configuration,
 * new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder(5),
 * new RetryConnectionAcquiringStrategy.Builder(2)
 * );
 * }
 * </pre>
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public class FlexyPoolDataSource implements DataSource, LifeCycleAware, ConnectionCallback {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FlexyPoolDataSource.class);
    public static final String OVERALL_CONNECTION_ACQUIRE_MILLIS = "overallConnectionAcquireMillis";
    public static final String CONCURRENT_CONNECTION_COUNT = "concurrentConnectionCountHistogram";
    public static final String CONNECTION_LEASE_MILLIS = "connectionLeaseMillis";

    private final DataSource targetDataSource;
    private final Metrics metrics;
    private final Timer connectionAcquireTotalTimer;
    private final Histogram concurrentConnectionCountHistogram;
    private final Timer connectionLeaseTimer;
    private final ConnectionProxyBuilder connectionProxyBuilder;
    private final Collection<ConnectionAcquiringStrategy> connectionAcquiringStrategies =
            new LinkedHashSet<ConnectionAcquiringStrategy>();

    private AtomicLong concurrentConnectionCount = new AtomicLong();

    public FlexyPoolDataSource(final Configuration configuration,
                               ConnectionAcquiringStrategyBuilder... strategyBuilders) {
        this.targetDataSource = configuration.getPoolAdapter().getTargetDataSource();
        this.metrics = configuration.getMetrics();
        this.connectionAcquireTotalTimer = metrics.timer(OVERALL_CONNECTION_ACQUIRE_MILLIS);
        this.concurrentConnectionCountHistogram = metrics.histogram(CONCURRENT_CONNECTION_COUNT);
        this.connectionLeaseTimer = metrics.timer(CONNECTION_LEASE_MILLIS);
        this.connectionProxyBuilder = configuration.getConnectionProxyBuilder();
        if (strategyBuilders.length == 0) {
            throw new IllegalArgumentException("The flexy pool pool must use at least one strategy!");
        }
        for (ConnectionAcquiringStrategyBuilder strategyBuilder : strategyBuilders) {
            connectionAcquiringStrategies.add(strategyBuilder.build(configuration));
        }
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
        long startNanos = System.nanoTime();
        try {
            Connection connection = null;
            for (ConnectionAcquiringStrategy strategy : connectionAcquiringStrategies) {
                try {
                    connection = strategy.getConnection(context);
                    break;
                } catch (AcquireTimeoutException e) {
                    LOGGER.warn("Couldn't retrieve connection from strategy {} with context {}", strategy, context);
                }
            }
            if (connection != null) {
                return connection;
            } else {
                throw new CantAcquireConnectionException();
            }
        } finally {
            long endNanos = System.nanoTime();
            connectionAcquireTotalTimer.update(TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void acquire(Connection connection) {
        concurrentConnectionCountHistogram.update(concurrentConnectionCount.incrementAndGet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release(Connection connection, long durationNanos) {
        concurrentConnectionCountHistogram.update(concurrentConnectionCount.decrementAndGet());
        connectionLeaseTimer.update(durationNanos, TimeUnit.NANOSECONDS);
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

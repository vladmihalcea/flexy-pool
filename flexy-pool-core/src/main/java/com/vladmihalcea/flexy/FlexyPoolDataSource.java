package com.vladmihalcea.flexy;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.exception.CantAcquireConnectionException;
import com.vladmihalcea.flexy.metric.Timer;
import com.vladmihalcea.flexy.strategy.ConnectionAcquiringStrategy;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * FlexyPoolDataSource - Flexible Pooling DataSource.
 *
 * It uses chaining strategies for acquiring connections.
 *
 * @author Vlad Mihalcea
 */
public class FlexyPoolDataSource implements DataSource {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FlexyPoolDataSource.class);
    public static final String OVERALL_CONNECTION_ACQUIRE_MILLIS = "overallConnectionAcquireMillis";

    private final DataSource targetDataSource;
    private final Timer connectionAcquireTotalTimer;
    private final Collection<? extends ConnectionAcquiringStrategy> connectionAcquiringStrategies;

    public FlexyPoolDataSource(final Configuration configuration,
                               ConnectionAcquiringStrategy... connectionAcquiringStrategies) {
        this.targetDataSource = configuration.getPoolAdapter().getTargetDataSource();
        this.connectionAcquireTotalTimer = configuration.getMetrics().timer(OVERALL_CONNECTION_ACQUIRE_MILLIS);
        if(connectionAcquiringStrategies.length == 0) {
            throw new IllegalArgumentException("The flexy pool must use at least one strategy!");
        }
        this.connectionAcquiringStrategies = Arrays.asList(connectionAcquiringStrategies);
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
}

package com.vladmihalcea.flexy;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.metric.Timer;
import com.vladmihalcea.flexy.strategy.ConnectionAcquiringStrategy;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * FlexyPoolDataSource - Flexible Pooling DataSource.
 *
 * It uses chainable strategies for acquiring connections.
 *
 * @author Vlad Mihalcea
 */
public class FlexyPoolDataSource implements DataSource {

    public static final String OVERALL_CONNECTION_ACQUIRE_MILLIS = "overallConnectionAcquireMillis";

    private final ConnectionAcquiringStrategy connectionAcquiringStrategy;
    private final DataSource targetDataSource;
    private final Timer connectionAcquireTotalTimer;

    public FlexyPoolDataSource(final Configuration configuration, final ConnectionAcquiringStrategy connectionAcquiringStrategy) {
        this.connectionAcquiringStrategy = connectionAcquiringStrategy;
        this.targetDataSource = configuration.getPoolAdapter().getTargetDataSource();
        this.connectionAcquireTotalTimer = configuration.getMetrics().timer(OVERALL_CONNECTION_ACQUIRE_MILLIS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        long startNanos = System.nanoTime();
        try {
            return connectionAcquiringStrategy.getConnection(
                    new ConnectionRequestContext.Builder()
                            .build());
        } finally {
            long endNanos = System.nanoTime();
            connectionAcquireTotalTimer.update(TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        long startNanos = System.nanoTime();
        try {
            return connectionAcquiringStrategy.getConnection(
                    new ConnectionRequestContext.Builder()
                            .setCredentials(new Credentials(username, password))
                            .build());
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

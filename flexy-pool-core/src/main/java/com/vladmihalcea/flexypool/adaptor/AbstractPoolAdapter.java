package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


/**
 * <code>AbstractPoolAdapter</code> defines the base behavior for obtaining a target connection.
 * The connection acquiring timing statistics is stored within the {@link AbstractPoolAdapter#connectionAcquireTimer}
 * This class is meant to be extended by specific pool adapter providers {DBCP, C3PO, Bitronix Transaction Manager}
 * <p/>
 * <p>Make sure you supply the adapting pool specific exception transaction mechanism {@link AbstractPoolAdapter#translateException}
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 * @see com.vladmihalcea.flexypool.adaptor.PoolAdapter
 */
public abstract class AbstractPoolAdapter<T extends DataSource> implements PoolAdapter<T> {

    public static final String CONNECTION_ACQUIRE_MILLIS = "connectionAcquireMillis";

    private final T targetDataSource;

    private final Timer connectionAcquireTimer;

    public AbstractPoolAdapter(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties) {
        this.targetDataSource = configurationProperties.getTargetDataSource();
        this.connectionAcquireTimer = configurationProperties.getMetrics().timer(CONNECTION_ACQUIRE_MILLIS);
    }

    /**
     * Get the target data source. This is the connection pool actual data source.
     *
     * @return target data source
     */
    @Override
    public T getTargetDataSource() {
        return targetDataSource;
    }

    /**
     * Get a connection from the targeted data source using the supplied Credentials.
     * The acquiring time is stored in the {@link AbstractPoolAdapter#connectionAcquireTimer}.
     *
     * @param requestContext connection request context
     * @return connection
     * @throws SQLException if a pool or a database error occurs
     */
    @Override
    public Connection getConnection(ConnectionRequestContext requestContext) throws SQLException {
        try {
            Credentials credentials = requestContext.getCredentials();
            long startNanos = System.nanoTime();
            try {
                return (credentials == null) ?
                        targetDataSource.getConnection() :
                        targetDataSource.getConnection(credentials.getUsername(), credentials.getPassword());
            } finally {
                long endNanos = System.nanoTime();
                connectionAcquireTimer.update(TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos), TimeUnit.MILLISECONDS);
            }
        } catch (SQLException e) {
            throw translateException(e);
        } catch (RuntimeException e) {
            throw translateException(e);
        }
    }

    /**
     * Translate the thrown exception to {@link com.vladmihalcea.flexypool.exception.AcquireTimeoutException}.
     *
     * @param e caught exception
     * @return translated exception
     */
    protected SQLException translateException(Exception e) {
        return new SQLException(e);
    }
}

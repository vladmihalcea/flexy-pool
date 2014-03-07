package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.metric.Timer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * AbstractPoolAdapter - Abstract class for PoolAdapter instances.
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractPoolAdapter<T extends DataSource> implements PoolAdapter {

    public static final String CONNECTION_ACQUIRE_MILLIS = "connectionAcquireMillis";

    private final T poolingDataSource;

    private final DataSource targetDataSource;

    private final Timer connectionAcquireTimer;

    public AbstractPoolAdapter(Metrics metrics, T poolingDataSource, DataSource targetDataSource) {
        this.poolingDataSource = poolingDataSource;
        this.targetDataSource = targetDataSource;
        this.connectionAcquireTimer = metrics.timer(CONNECTION_ACQUIRE_MILLIS);
    }

    public AbstractPoolAdapter(Metrics metrics, T poolingDataSource) {
        this(metrics, poolingDataSource, poolingDataSource);
    }

    @Override
    public DataSource getTargetDataSource() {
        return targetDataSource;
    }

    protected T getPoolingDataSource() {
        return poolingDataSource;
    }

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
            throw launderSQLException(e);
        } catch (RuntimeException e) {
            throw launderRuntimeException(e);
        }
    }

    protected SQLException launderSQLException(SQLException e) {
        return e;
    }

    protected RuntimeException launderRuntimeException(RuntimeException e) {
        return e;
    }
}

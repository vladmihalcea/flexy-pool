package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.event.ConnectionAcquireTimeoutEvent;
import com.vladmihalcea.flexypool.event.EventPublisher;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


/**
 * <code>AbstractPoolAdapter</code> defines the base behavior for obtaining a target connection.
 * The connection acquiring timing statistics is stored within the {@link AbstractPoolAdapter#connectionAcquireTimer}
 * This class is meant to be extended by specific pool adapter providers {DBCP, C3PO, Bitronix Transaction Manager}
 * <p>
 * <p>Make sure you supply the adapting pool specific exception transaction mechanism {@link AbstractPoolAdapter#translateException}
 *
 * @author Vlad Mihalcea
 * @see com.vladmihalcea.flexypool.adaptor.PoolAdapter
 * @since 1.0
 */
public abstract class AbstractPoolAdapter<T extends DataSource> implements PoolAdapter<T> {

    public static final String CONNECTION_ACQUIRE_MILLIS = "connectionAcquireMillis";

    private final ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties;

    private final T targetDataSource;

    private final Timer connectionAcquireTimer;

    private final EventPublisher eventPublisher;

    public AbstractPoolAdapter(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties) {
        this.configurationProperties = configurationProperties;
        this.targetDataSource = configurationProperties.getTargetDataSource();
        this.connectionAcquireTimer = configurationProperties.getMetrics().timer(CONNECTION_ACQUIRE_MILLIS);
        this.eventPublisher = configurationProperties.getEventPublisher();
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
        long startNanos = System.nanoTime();
        try {
            Credentials credentials = requestContext.getCredentials();
            return (credentials == null) ?
                    targetDataSource.getConnection() :
                    targetDataSource.getConnection(credentials.getUsername(), credentials.getPassword());
        } catch (SQLException e) {
            throw translateException(e);
        } catch (RuntimeException e) {
            throw translateException(e);
        } finally {
            long endNanos = System.nanoTime();
            connectionAcquireTimer.update(TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Translate the thrown exception to {@link com.vladmihalcea.flexypool.exception.AcquireTimeoutException}.
     *
     * @param e caught exception
     * @return translated exception
     */
    protected SQLException translateException(Exception e) {
        if (isAcquireTimeoutException(e)) {
            eventPublisher.publish(new ConnectionAcquireTimeoutEvent(configurationProperties.getUniqueName()));
            return new AcquireTimeoutException(e);
        } else if (e instanceof SQLException) {
            return (SQLException) e;
        }
        return new SQLException(e);
    }

    /**
     * Check if the caught exception is due to a connection acquire failure
     *
     * @param e exception to be checked
     * @return the exception is due to a connection acquire failure
     */
    protected abstract boolean isAcquireTimeoutException(Exception e);
}

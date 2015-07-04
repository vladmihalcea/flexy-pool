package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

/**
 * <code>Java7ManagedConnection</code> wraps a given {@link Connection} and delegates all calls to it.
 * It extends {@link ConnectionDecorator} and adds the Java 7 new methods.
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public class Java7ConnectionDecorator extends ConnectionDecorator {

    public Java7ConnectionDecorator(Connection target, ConnectionCallback callback) {
        super(target, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSchema(String schema) throws SQLException {
        getTarget().setSchema(schema);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchema() throws SQLException {
        return getTarget().getSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abort(Executor executor) throws SQLException {
        getTarget().abort(executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getTarget().setNetworkTimeout(executor, milliseconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return getTarget().getNetworkTimeout();
    }
}

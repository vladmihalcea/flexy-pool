package com.vladmihalcea.flexy;

import com.vladmihalcea.flexy.strategy.ConnectionAcquiringStrategy;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * FlexyPoolDataSource - Flexible Pooling DataSource.
 *
 * It uses chainable strategies for acquiring connections.
 *
 * @author Vlad Mihalcea
 */
public class FlexyPoolDataSource implements DataSource {

    private final ConnectionAcquiringStrategy connectionAcquiringStrategy;
    private final DataSource dataSource;

    public FlexyPoolDataSource(final ConnectionAcquiringStrategy connectionAcquiringStrategy) {
        this.connectionAcquiringStrategy = connectionAcquiringStrategy;
        this.dataSource = connectionAcquiringStrategy.getPoolAdapter().getDataSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return connectionAcquiringStrategy.getConnection(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return connectionAcquiringStrategy.getConnection(new ConnectionCredentials(username, password));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }
}

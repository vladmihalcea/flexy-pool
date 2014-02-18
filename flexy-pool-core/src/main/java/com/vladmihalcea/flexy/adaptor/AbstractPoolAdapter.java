package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.ConnectionCredentials;
import com.vladmihalcea.flexy.PoolAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * AbstractPoolAdapter - Abstract class for PoolAdapter instances.
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractPoolAdapter<T extends DataSource> implements PoolAdapter {

    private final T dataSource;

    public AbstractPoolAdapter(T dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public T getDataSource() {
        return dataSource;
    }

    @Override
    public Connection getConnection(ConnectionCredentials credentials) throws SQLException {
        try {
            return (credentials == null) ?
                    dataSource.getConnection() :
                    dataSource.getConnection(credentials.getUsername(), credentials.getPassword());
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

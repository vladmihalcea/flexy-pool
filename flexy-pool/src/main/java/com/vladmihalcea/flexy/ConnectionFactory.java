package com.vladmihalcea.flexy;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ConnectionFactory - Factory for obtaining connections from the pool
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionFactory {

    /**
     * Get connection from the pool.
     * @param credentials connection credentials
     * @return pooled connection
     * @throws SQLException in case of errors
     */
    Connection getConnection(ConnectionCredentials credentials) throws SQLException;
}

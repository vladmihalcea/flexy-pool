package com.vladmihalcea.flexy.connection;

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
     * @param requestContext connection request context
     * @return pooled connection
     * @throws SQLException in case of errors
     */
    Connection getConnection(ConnectionRequestContext requestContext) throws SQLException;
}

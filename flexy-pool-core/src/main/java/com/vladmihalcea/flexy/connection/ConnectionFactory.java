package com.vladmihalcea.flexy.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <code>ConnectionFactory</code> abstract the way we retrieve a database connection.
 * It uses a single {@link ConnectionRequestContext} parameter to simplify the acquiring options.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public interface ConnectionFactory {

    /**
     * Get connection from the pool/database.
     *
     * @param requestContext connection request context
     * @return pooled connection
     * @throws SQLException in case the pool/database throws errors
     */
    Connection getConnection(ConnectionRequestContext requestContext) throws SQLException;
}

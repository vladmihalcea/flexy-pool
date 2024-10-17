package com.vladmihalcea.flexypool.exception;

import java.sql.SQLException;

/**
 * <code>ConnectionAcquisitionException</code> is thrown when a connection couldn't be obtained from the pool after
 * all possible attempts.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class ConnectionAcquisitionException extends SQLException {

    private static final long serialVersionUID = 2752173976156070744L;

    /**
     * Couldn't acquire connection
     *
     * @param reason connection acquisition failing reason
     */
    public ConnectionAcquisitionException(String reason) {
        super(reason);
    }
}

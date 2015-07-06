package com.vladmihalcea.flexypool.exception;

import java.sql.SQLException;

/**
 * <code>CantAcquireConnectionException</code> is thrown when a connection couldn't be obtained from the pool after
 * all possible attempts.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class CantAcquireConnectionException extends SQLException {

    private static final long serialVersionUID = 2752173976156070744L;

    /**
     * Couldn't acquire connection
     *
     * @param reason connection acquire failing reason
     */
    public CantAcquireConnectionException(String reason) {
        super(reason);
    }
}

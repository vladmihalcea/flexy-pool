package com.vladmihalcea.flexypool.exception;

import java.sql.SQLException;

/**
 * <code>CantAcquireConnectionException</code> is thrown when a connection couldn't be obtained from the pool after
 * all possible attempts.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class CantAcquireConnectionException extends SQLException {

    public CantAcquireConnectionException() {
        super();
    }
}

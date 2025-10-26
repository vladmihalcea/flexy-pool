package com.vladmihalcea.flexypool.exception;

import java.sql.SQLException;

/**
 * <code>ConnectionAcquisitionTimeoutException</code> is thrown when a connection couldn't be obtained from the pool
 * in less than the pool timeout interval.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class ConnectionAcquisitionTimeoutException extends SQLException {

    private static final long serialVersionUID = -5632855297822130922L;

    /**
     * Store the originating cause.
     *
     * @param cause cause
     */
    public ConnectionAcquisitionTimeoutException(Throwable cause) {
        super(cause);
    }
}

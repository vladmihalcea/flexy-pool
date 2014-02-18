package com.vladmihalcea.flexy.exception;

import java.sql.SQLException;

/**
 * AcquireTimeoutException - Exception marking a an acquiring timeout
 *
 * @author Vlad Mihalcea
 */
public class AcquireTimeoutException extends SQLException {

    public AcquireTimeoutException(Throwable cause) {
        super(cause);
    }
}

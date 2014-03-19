package com.vladmihalcea.flexy.exception;

import java.sql.SQLException;

/**
 * CantAcquireConnectionException - Exception when the connection can't be acquired
 *
 * @author Vlad Mihalcea
 */
public class CantAcquireConnectionException extends SQLException {

    public CantAcquireConnectionException() {
        super();
    }
}

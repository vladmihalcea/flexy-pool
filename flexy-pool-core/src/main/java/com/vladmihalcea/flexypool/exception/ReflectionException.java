package com.vladmihalcea.flexypool.exception;

/**
 * <code>ReflectionException</code> is thrown when an exception is caught during a reflection call.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = -8241199836349576388L;

    /**
     * Store the originating cause.
     *
     * @param cause cause
     */
    public ReflectionException(Throwable cause) {
        super(cause);
    }
}

package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.connection.ConnectionFactory;

/**
 * <code>ConnectionAcquiringStrategy</code> is at its core a {@link com.vladmihalcea.flexy.connection.ConnectionFactory}
 * working as a connection acquiring interceptor.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public interface ConnectionAcquiringStrategy extends ConnectionFactory {

}

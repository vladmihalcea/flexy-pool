package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.connection.ConnectionFactory;

/**
 * <code>ConnectionAcquisitionStrategy</code> is at its core a {@link com.vladmihalcea.flexypool.connection.ConnectionFactory}
 * working as a connection acquiring interceptor.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public interface ConnectionAcquisitionStrategy extends ConnectionFactory {

}

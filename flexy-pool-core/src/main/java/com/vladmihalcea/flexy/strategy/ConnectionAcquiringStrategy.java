package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.connection.ConnectionFactory;
import com.vladmihalcea.flexy.adaptor.PoolAdapterAware;

/**
 * ConnectionAcquiringStrategy - Base interface for all connection acquiring strategies.
 *
 * @author Vlad Mihalcea
 */
public interface ConnectionAcquiringStrategy extends ConnectionFactory, PoolAdapterAware {

}

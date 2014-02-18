package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.ConnectionFactory;
import com.vladmihalcea.flexy.PoolAdapter;

/**
 * AbstractConnectionAcquiringStrategy - Abstract base class for all connection acquiring strategies
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final PoolAdapter poolAdapter;
    private final ConnectionFactory connectionFactory;

    protected AbstractConnectionAcquiringStrategy(PoolAdapter poolAdapter) {
        this.poolAdapter = poolAdapter;
        this.connectionFactory = poolAdapter;
    }

    protected AbstractConnectionAcquiringStrategy(ConnectionAcquiringStrategy connectionAcquiringStrategy) {
        this.poolAdapter = connectionAcquiringStrategy.getPoolAdapter();
        this.connectionFactory = connectionAcquiringStrategy;
    }

    /**
     * Get the connection factory.
     * @return connection factory
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * Get the pool adaptor.
     * @return  pool adaptor
     */
    public PoolAdapter getPoolAdapter() {
        return poolAdapter;
    }
}

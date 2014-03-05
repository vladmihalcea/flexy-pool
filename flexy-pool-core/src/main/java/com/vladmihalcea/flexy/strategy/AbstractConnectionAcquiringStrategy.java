package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.connection.ConnectionFactory;
import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.context.Context;

/**
 * AbstractConnectionAcquiringStrategy - Abstract base class for all connection acquiring strategies
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final Context context;
    private final PoolAdapter poolAdapter;
    private final ConnectionFactory connectionFactory;

    protected AbstractConnectionAcquiringStrategy(Context context, PoolAdapter poolAdapter) {
        this.context = context;
        this.poolAdapter = poolAdapter;
        this.connectionFactory = poolAdapter;
    }

    protected AbstractConnectionAcquiringStrategy(Context context, ConnectionAcquiringStrategy connectionAcquiringStrategy) {
        this.context = context;
        this.poolAdapter = connectionAcquiringStrategy.getPoolAdapter();
        this.connectionFactory = connectionAcquiringStrategy;
    }

    /**
     * Get context
     * @return context
     */
    public Context getContext() {
        return context;
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

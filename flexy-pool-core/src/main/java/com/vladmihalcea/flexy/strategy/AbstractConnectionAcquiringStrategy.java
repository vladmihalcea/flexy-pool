package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.connection.ConnectionFactory;
import com.vladmihalcea.flexy.context.Context;

/**
 * AbstractConnectionAcquiringStrategy - Abstract base class for all connection acquiring strategies
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractConnectionAcquiringStrategy implements ConnectionAcquiringStrategy {

    private final Context context;
    private final ConnectionFactory connectionFactory;

    protected AbstractConnectionAcquiringStrategy(Context context, ConnectionAcquiringStrategy connectionAcquiringStrategy) {
        this.context = context;
        this.connectionFactory = connectionAcquiringStrategy != null ? connectionAcquiringStrategy : context.getPoolAdapter();
    }

    protected AbstractConnectionAcquiringStrategy(Context context) {
        this(context, null);
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
}

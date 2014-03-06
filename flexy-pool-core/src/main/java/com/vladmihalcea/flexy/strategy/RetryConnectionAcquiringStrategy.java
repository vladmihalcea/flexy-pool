package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.context.Context;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * RetryConnectionAcquiringStrategy - Retry pool strategy.
 *
 * @author Vlad Mihalcea
 */
public class RetryConnectionAcquiringStrategy extends AbstractConnectionAcquiringStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryConnectionAcquiringStrategy.class);

    private final int retryAttempts;

    public RetryConnectionAcquiringStrategy(Context context, PoolAdapter poolAdapter, int retryAttempts) {
        super(context, poolAdapter);
        this.retryAttempts = validateRetryAttempts(retryAttempts);
    }

    public RetryConnectionAcquiringStrategy(Context context, ConnectionAcquiringStrategy connectionAcquiringStrategy, int retryAttempts) {
        super(context, connectionAcquiringStrategy);
        this.retryAttempts = validateRetryAttempts(retryAttempts);
    }

    private int validateRetryAttempts(int retryAttempts) {
        if(retryAttempts <= 0) {
            throw new IllegalArgumentException("retryAttempts must ge greater than 0!");
        }
        return retryAttempts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(ConnectionRequestContext context) throws SQLException {
        int remainingAttempts = retryAttempts;
        do {
            try {
                context.incrementAttempts();
                return getConnectionFactory().getConnection(context);
            } catch (AcquireTimeoutException e) {
                remainingAttempts--;
                LOGGER.info("Can't acquire connection, remaining retry attempts {}", remainingAttempts);
                if(remainingAttempts < 0 ) {
                    throw e;
                }
            }
        } while (true);
    }
}

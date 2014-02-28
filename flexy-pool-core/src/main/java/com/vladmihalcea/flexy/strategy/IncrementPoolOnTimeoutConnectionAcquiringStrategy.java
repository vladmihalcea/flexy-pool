package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.ConnectionRequestContext;
import com.vladmihalcea.flexy.Credentials;
import com.vladmihalcea.flexy.PoolAdapter;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * IncrementPoolOnTimeoutConnectionAcquiringStrategy - Pool size increment on timeout strategy.
 *
 * @author Vlad Mihalcea
 */
public class IncrementPoolOnTimeoutConnectionAcquiringStrategy extends AbstractConnectionAcquiringStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementPoolOnTimeoutConnectionAcquiringStrategy.class);

    private final Lock lock = new ReentrantLock();

    private final int maxOverflowPoolSize;

    public IncrementPoolOnTimeoutConnectionAcquiringStrategy(PoolAdapter poolAdapter, int maxOverflowPoolSize) {
        super(poolAdapter);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
        if(getMaxOverflowPoolSize() <= poolAdapter.getMaxPoolSize()) {
            throw new IllegalStateException("The pool has already reached the max pool size!");
        }
    }

    public IncrementPoolOnTimeoutConnectionAcquiringStrategy(ConnectionAcquiringStrategy connectionAcquiringStrategy, int maxOverflowPoolSize) {
        super(connectionAcquiringStrategy);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
    }

    public int getMaxOverflowPoolSize() {
        return maxOverflowPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(ConnectionRequestContext context) throws SQLException {
        do {
            try {
                return getConnectionFactory().getConnection(context);
            } catch (AcquireTimeoutException e) {
                if(!incrementPoolSize(context)) {
                    LOGGER.info("Can't acquire connection, pool size has already overflown to its max size.");
                    throw e;
                }
            }
        } while (true);
    }

    /**
     * Attempt to increment the pool size
     * @return has pool size changed
     */
    protected boolean incrementPoolSize(ConnectionRequestContext context) {
        boolean incremented = false;
        try {
            lock.lockInterruptibly();
            int previousMaxSize = getPoolAdapter().getMaxPoolSize();
            incremented = previousMaxSize < maxOverflowPoolSize;
            if(incremented) {
                getPoolAdapter().setMaxPoolSize(previousMaxSize + 1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        if (incremented) {
            LOGGER.info("Pool size changed to {}", getPoolAdapter().getMaxPoolSize());
            context.incrementOverflowPoolSize();
        }
        return incremented;
    }
}

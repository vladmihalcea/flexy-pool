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
        try {
            return getConnectionFactory().getConnection(context);
        } catch (AcquireTimeoutException e) {
            if(incrementPoolSize()) {
                LOGGER.info("Can't acquire connection, pool size incremented.");
                return getConnection(context);
            }
            throw e;
        }
    }

    /**
     * Attempt to increment the pool size
     * @return has pool size changed
     */
    protected boolean incrementPoolSize() {
        boolean incremented = false;
        try {
            lock.lockInterruptibly();
            int previousMaxSize = getPoolAdapter().getMaxPoolSize();
            if(previousMaxSize < getMaxOverflowPoolSize()) {
                int nextMaxSize = previousMaxSize + 1;
                getPoolAdapter().setMaxPoolSize(nextMaxSize);
                incremented = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        if (incremented) {
            LOGGER.info("Pool size changed to {}", getPoolAdapter().getMaxPoolSize());
        }
        return incremented;
    }
}

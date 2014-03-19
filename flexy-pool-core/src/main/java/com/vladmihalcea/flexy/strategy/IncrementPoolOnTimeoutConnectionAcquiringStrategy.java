package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.metric.Histogram;
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

    public static final String MAX_POOL_SIZE_HISTOGRAM = "maxPoolSizeHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementPoolOnTimeoutConnectionAcquiringStrategy.class);

    private final Lock lock = new ReentrantLock();

    private final int maxOverflowPoolSize;

    private final Histogram maxPoolSizeHistogram;
    
    private final PoolAdapter poolAdapter;

    public IncrementPoolOnTimeoutConnectionAcquiringStrategy(Configuration configuration, int maxOverflowPoolSize) {
        super(configuration);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
        this.maxPoolSizeHistogram = configuration.getMetrics().histogram(MAX_POOL_SIZE_HISTOGRAM);
        maxPoolSizeHistogram.update(configuration.getPoolAdapter().getMaxPoolSize());
        poolAdapter = configuration.getPoolAdapter();
    }

    public int getMaxOverflowPoolSize() {
        return maxOverflowPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(ConnectionRequestContext requestContext) throws SQLException {
        do {
            int expectingMaxSize = poolAdapter.getMaxPoolSize();
            try {
                return getConnectionFactory().getConnection(requestContext);
            } catch (AcquireTimeoutException e) {
                if(!incrementPoolSize(expectingMaxSize)) {
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
    protected boolean incrementPoolSize(int expectingMaxSize) {
        
        Integer maxSize = null;
        try {
            lock.lockInterruptibly();
            int currentMaxSize = poolAdapter.getMaxPoolSize();
            boolean incrementMaxPoolSize = currentMaxSize < maxOverflowPoolSize;
            if(currentMaxSize > expectingMaxSize) {
                LOGGER.info("Pool size changed by other thread, expected {} and actual value {}", expectingMaxSize, currentMaxSize);
                return incrementMaxPoolSize;
            }
            if(!incrementMaxPoolSize) {
                return false;
            }
            poolAdapter.setMaxPoolSize(++currentMaxSize);
            maxSize = currentMaxSize;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
        LOGGER.info("Pool size changed from previous value {} to {}", expectingMaxSize, maxSize);
        maxPoolSizeHistogram.update(maxSize);
        return true;
    }

    @Override
    public String toString() {
        return "IncrementPoolOnTimeoutConnectionAcquiringStrategy{" +
                "maxOverflowPoolSize=" + maxOverflowPoolSize +
                '}';
    }
}

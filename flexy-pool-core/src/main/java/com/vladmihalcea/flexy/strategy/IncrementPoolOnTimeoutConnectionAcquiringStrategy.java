package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.context.Context;
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

    public static final String MIN_POOL_SIZE_HISTOGRAM = "minPoolSizeHistogram";
    public static final String MAX_POOL_SIZE_HISTOGRAM = "maxPoolSizeHistogram";
    public static final String OVERFLOW_POOL_SIZE_HISTOGRAM = "overflowPoolSizeHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementPoolOnTimeoutConnectionAcquiringStrategy.class);

    private final Lock lock = new ReentrantLock();

    private final int maxOverflowPoolSize;

    private final Histogram minPoolSizeHistogram;
    private final Histogram maxPoolSizeHistogram;
    private final Histogram overflowPoolSizeHistogram;

    public IncrementPoolOnTimeoutConnectionAcquiringStrategy(Context context, PoolAdapter poolAdapter, int maxOverflowPoolSize) {
        super(context, poolAdapter);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
        this.minPoolSizeHistogram = context.getMetrics().histogram(MIN_POOL_SIZE_HISTOGRAM);
        this.maxPoolSizeHistogram = context.getMetrics().histogram(MAX_POOL_SIZE_HISTOGRAM);
        this.overflowPoolSizeHistogram = context.getMetrics().histogram(OVERFLOW_POOL_SIZE_HISTOGRAM);
    }

    public IncrementPoolOnTimeoutConnectionAcquiringStrategy(Context context, ConnectionAcquiringStrategy connectionAcquiringStrategy, int maxOverflowPoolSize) {
        super(context, connectionAcquiringStrategy);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
        this.minPoolSizeHistogram = context.getMetrics().histogram(MIN_POOL_SIZE_HISTOGRAM);
        this.maxPoolSizeHistogram = context.getMetrics().histogram(MAX_POOL_SIZE_HISTOGRAM);
        this.overflowPoolSizeHistogram = context.getMetrics().histogram(OVERFLOW_POOL_SIZE_HISTOGRAM);
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
        Integer minSize = null;
        Integer maxSize = null;
        try {
            lock.lockInterruptibly();
            minSize = getPoolAdapter().getMinPoolSize();
            maxSize = getPoolAdapter().getMaxPoolSize();
            incremented = maxSize < maxOverflowPoolSize;
            if(incremented) {
                getPoolAdapter().setMaxPoolSize(maxSize + 1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        if (incremented) {
            LOGGER.info("Pool size changed to {}", maxSize);
            context.incrementOverflowPoolSize();
            minPoolSizeHistogram.update(minSize);
            maxPoolSizeHistogram.update(maxSize);
            maxPoolSizeHistogram.update(maxSize + 1);
            overflowPoolSizeHistogram.update(context.getOverflowPoolSize());
        }
        return incremented;
    }
}

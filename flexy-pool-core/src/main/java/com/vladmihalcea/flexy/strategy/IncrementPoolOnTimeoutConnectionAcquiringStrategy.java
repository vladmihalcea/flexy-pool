package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.metric.Histogram;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.util.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <code>IncrementPoolOnTimeoutConnectionAcquiringStrategy</code> extends the {@link AbstractConnectionAcquiringStrategy}
 * and it allows the pool size to grow beyond its {@link com.vladmihalcea.flexy.adaptor.PoolAdapter#getMaxPoolSize()}
 * up to reaching the {@link IncrementPoolOnTimeoutConnectionAcquiringStrategy#maxOverflowPoolSize} limit.
 * <p/>
 * Use this strategy to dynamically adjust the pool size based on the connection acquiring demand.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public final class IncrementPoolOnTimeoutConnectionAcquiringStrategy<T extends DataSource> extends AbstractConnectionAcquiringStrategy {

    public static final String MAX_POOL_SIZE_HISTOGRAM = "maxPoolSizeHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementPoolOnTimeoutConnectionAcquiringStrategy.class);

    /**
     * The {@link com.vladmihalcea.flexy.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder} class allows
     * creating this strategy for a given {@link com.vladmihalcea.flexy.util.ConfigurationProperties}
     */
    public static class Builder<T extends DataSource> implements ConnectionAcquiringStrategyBuilder<IncrementPoolOnTimeoutConnectionAcquiringStrategy, T> {
        private final int maxOverflowPoolSize;

        public Builder(int maxOverflowPoolSize) {
            this.maxOverflowPoolSize = maxOverflowPoolSize;
        }

        /**
         * Build a {@link com.vladmihalcea.flexy.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy} for a given
         * {@link com.vladmihalcea.flexy.util.ConfigurationProperties}
         *
         * @param configurationProperties configurationProperties
         * @return strategy
         */
        public IncrementPoolOnTimeoutConnectionAcquiringStrategy build(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties) {
            return new IncrementPoolOnTimeoutConnectionAcquiringStrategy(
                    configurationProperties, maxOverflowPoolSize
            );
        }
    }

    private final Lock lock = new ReentrantLock();

    private final int maxOverflowPoolSize;

    private final Histogram maxPoolSizeHistogram;

    private final PoolAdapter poolAdapter;

    /**
     * Create the strategy for the given configurationProperties and the maxOverflowPoolSize.
     *
     * @param configurationProperties configurationProperties
     * @param maxOverflowPoolSize     maximum overflowing pool sizing
     */
    private IncrementPoolOnTimeoutConnectionAcquiringStrategy(ConfigurationProperties<? extends DataSource, Metrics, PoolAdapter> configurationProperties, int maxOverflowPoolSize) {
        super(configurationProperties);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
        this.maxPoolSizeHistogram = configurationProperties.getMetrics().histogram(MAX_POOL_SIZE_HISTOGRAM);
        maxPoolSizeHistogram.update(configurationProperties.getPoolAdapter().getMaxPoolSize());
        poolAdapter = configurationProperties.getPoolAdapter();
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
                if (!incrementPoolSize(expectingMaxSize)) {
                    LOGGER.info("Can't acquire connection, pool size has already overflown to its max size.");
                    throw e;
                }
            }
        } while (true);
    }

    /**
     * Attempt to increment the pool size. If the maxSize changes, it skips the incrementing process.
     *
     * @return if it succeeded changing the pool size
     */
    protected boolean incrementPoolSize(int expectingMaxSize) {

        Integer maxSize = null;
        try {
            lock.lockInterruptibly();
            int currentMaxSize = poolAdapter.getMaxPoolSize();
            boolean incrementMaxPoolSize = currentMaxSize < maxOverflowPoolSize;
            if (currentMaxSize > expectingMaxSize) {
                LOGGER.info("Pool size changed by other thread, expected {} and actual value {}", expectingMaxSize, currentMaxSize);
                return incrementMaxPoolSize;
            }
            if (!incrementMaxPoolSize) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "IncrementPoolOnTimeoutConnectionAcquiringStrategy{" +
                "maxOverflowPoolSize=" + maxOverflowPoolSize +
                '}';
    }
}

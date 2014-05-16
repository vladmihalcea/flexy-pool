package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <code>IncrementPoolOnTimeoutConnectionAcquiringStrategy</code> extends the {@link AbstractConnectionAcquiringStrategy}
 * and it allows the pool size to grow beyond its {@link com.vladmihalcea.flexypool.adaptor.PoolAdapter#getMaxPoolSize()}
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
    public static final String OVERFLOW_POOL_SIZE_HISTOGRAM = "overflowPoolSizeHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger(IncrementPoolOnTimeoutConnectionAcquiringStrategy.class);

    /**
     * The {@link com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory} class allows
     * creating this strategy for a given {@link com.vladmihalcea.flexypool.util.ConfigurationProperties}
     */
    public static class Factory<T extends DataSource> implements ConnectionAcquiringStrategyFactory<IncrementPoolOnTimeoutConnectionAcquiringStrategy, T> {

        private final int maxOverflowPoolSize;

        private final int timeoutMillis;

        public Factory(int maxOverflowPoolSize, int timeoutMillis) {
            this.maxOverflowPoolSize = maxOverflowPoolSize;
            this.timeoutMillis = timeoutMillis;
        }

        public Factory(int maxOverflowPoolSize) {
            this(maxOverflowPoolSize, Integer.MAX_VALUE);
        }

        /**
         * Creates a {@link com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy} for a given
         * {@link com.vladmihalcea.flexypool.util.ConfigurationProperties}
         *
         * @param configurationProperties configurationProperties
         * @return strategy
         */
        public IncrementPoolOnTimeoutConnectionAcquiringStrategy newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties) {
            return new IncrementPoolOnTimeoutConnectionAcquiringStrategy(
                    configurationProperties, maxOverflowPoolSize, timeoutMillis
            );
        }
    }

    private final Lock lock = new ReentrantLock();

    private final int maxOverflowPoolSize;

    private final int timeoutMillis;

    private AtomicLong overflowPoolSize = new AtomicLong();

    private final Histogram maxPoolSizeHistogram;

    private final Histogram overflowPoolSizeHistogram;

    private final PoolAdapter poolAdapter;

    /**
     * Create the strategy for the given configurationProperties and the maxOverflowPoolSize.
     *
     * @param configurationProperties configurationProperties
     * @param maxOverflowPoolSize     maximum overflowing pool sizing
     * @param timeoutMillis           if the connection acquiring time took more than this value a pool size increment is attempted
     */
    private IncrementPoolOnTimeoutConnectionAcquiringStrategy(ConfigurationProperties<? extends DataSource, Metrics, PoolAdapter> configurationProperties, int maxOverflowPoolSize, int timeoutMillis) {
        super(configurationProperties);
        this.maxOverflowPoolSize = maxOverflowPoolSize;
        this.timeoutMillis = timeoutMillis;
        this.maxPoolSizeHistogram = configurationProperties.getMetrics().histogram(MAX_POOL_SIZE_HISTOGRAM);
        this.overflowPoolSizeHistogram = configurationProperties.getMetrics().histogram(OVERFLOW_POOL_SIZE_HISTOGRAM);
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
                long startNanos = System.nanoTime();
                Connection connection = getConnectionFactory().getConnection(requestContext);
                long endNanos = System.nanoTime();
                long connectionAcquireDurationMillis = TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos);
                if(connectionAcquireDurationMillis > timeoutMillis) {
                    LOGGER.warn("Connection was acquired in {} millis, timeoutMillis is set to {}",
                            connectionAcquireDurationMillis, timeoutMillis);
                    int maxPoolSize = poolAdapter.getMaxPoolSize();
                    if(maxPoolSize < maxOverflowPoolSize) {
                        if(!incrementPoolSize(expectingMaxSize)) {
                            LOGGER.warn("Can't acquire connection, pool size has already overflown to its max size.");
                        }
                    } else {
                        LOGGER.info("Pool size has already overflown to its max size of {}", maxPoolSize);
                    }
                }
                return connection;
            } catch (AcquireTimeoutException e) {
                if (!incrementPoolSize(expectingMaxSize)) {
                    LOGGER.warn("Can't acquire connection due to adaptor timeout, pool size has already overflown to its max size.");
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
        long currentOverflowPoolSize;
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
            currentOverflowPoolSize = overflowPoolSize.incrementAndGet();
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
        overflowPoolSizeHistogram.update(currentOverflowPoolSize);
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

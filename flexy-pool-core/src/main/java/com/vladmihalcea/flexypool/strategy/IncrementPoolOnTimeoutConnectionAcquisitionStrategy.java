package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.exception.ConnectionAcquisitionTimeoutException;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
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
 * <code>IncrementPoolOnTimeoutConnectionAcquiringStrategy</code> extends the {@link AbstractConnectionAcquisitionStrategy}
 * and it allows the pool size to grow beyond its {@link com.vladmihalcea.flexypool.adaptor.PoolAdapter#getMaxPoolSize()}
 * up to reaching the {@link IncrementPoolOnTimeoutConnectionAcquisitionStrategy#maxOverflowPoolSize} limit.
 * <br>
 * Use this strategy to dynamically adjust the pool size based on the connection acquiring demand.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public final class IncrementPoolOnTimeoutConnectionAcquisitionStrategy<T extends DataSource> extends
        AbstractConnectionAcquisitionStrategy {

    public static final String MAX_POOL_SIZE_HISTOGRAM = "maxPoolSizeHistogram";
    public static final String OVERFLOW_POOL_SIZE_HISTOGRAM = "overflowPoolSizeHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger( IncrementPoolOnTimeoutConnectionAcquisitionStrategy.class);

    /**
     * The {@link IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory} class allows
     * creating this strategy for a given {@link ConfigurationProperties}
     */
    public static class Factory<T extends DataSource> implements ConnectionAcquisitionStrategyFactory<IncrementPoolOnTimeoutConnectionAcquisitionStrategy, T> {

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
         * Creates a {@link IncrementPoolOnTimeoutConnectionAcquisitionStrategy} for a given
         * {@link ConfigurationProperties}
         *
         * @param configurationProperties configurationProperties
         * @return strategy
         */
        public IncrementPoolOnTimeoutConnectionAcquisitionStrategy newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties) {
            return new IncrementPoolOnTimeoutConnectionAcquisitionStrategy(
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
    private IncrementPoolOnTimeoutConnectionAcquisitionStrategy(ConfigurationProperties<? extends DataSource, Metrics, PoolAdapter> configurationProperties, int maxOverflowPoolSize, int timeoutMillis) {
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
                long connectionAcquisitionDurationMillis = TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos);
                if (connectionAcquisitionDurationMillis > timeoutMillis) {
                    LOGGER.warn("Connection was acquired in {} millis, timeoutMillis is set to {}",
                            connectionAcquisitionDurationMillis, timeoutMillis);
                    int maxPoolSize = poolAdapter.getMaxPoolSize();
                    if (maxPoolSize < maxOverflowPoolSize) {
                        if (!incrementPoolSize(expectingMaxSize)) {
                            LOGGER.warn("Can't increase pool size because it has already overflown to its max size of {}", maxOverflowPoolSize);
                        }
                    } else {
                        LOGGER.info("Pool size has already overflown to its max size of {}", maxPoolSize);
                    }
                }
                return connection;
            } catch (ConnectionAcquisitionTimeoutException e) {
                if (!incrementPoolSize(expectingMaxSize)) {
                    LOGGER.warn("Can't acquire connection due to adaptor timeout, pool size has already overflown to its max size of {}", maxOverflowPoolSize);
                    throw e;
                }
            }
        } while (true);
    }

    /**
     * Attempt to increment the pool size. If the maxSize changes, it skips the incrementing process.
     *
     * @param expectingMaxSize expecting maximum pool size
     * @return if the pool size got changed from the expected max size
     */
	private boolean incrementPoolSize(int expectingMaxSize) {
        int maxSize;
        long currentOverflowPoolSize;
        boolean poolSizeChanged = false;
        try {
            lock.lockInterruptibly();
            int currentMaxSize = poolAdapter.getMaxPoolSize();
            if (currentMaxSize > expectingMaxSize) {
                LOGGER.info("Pool size was changed by other thread, expected a size of {}, but the actual value is {}", expectingMaxSize, currentMaxSize);
                return true;
            }
            if (currentMaxSize < maxOverflowPoolSize) {
                currentOverflowPoolSize = overflowPoolSize.incrementAndGet();
                poolAdapter.setMaxPoolSize(++currentMaxSize);
                poolSizeChanged = true;
                maxSize = currentMaxSize;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return poolSizeChanged;
        } finally {
            lock.unlock();
        }
        LOGGER.info("Pool size changed from {} to {} connections", expectingMaxSize, maxSize);
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

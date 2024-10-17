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

/**
 * <code>RetryConnectionAcquisitionStrategy</code> extends the {@link AbstractConnectionAcquisitionStrategy}
 * and it allows multiple acquiring attempts before giving up by rethrowing the {@link ConnectionAcquisitionTimeoutException}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public final class RetryConnectionAcquisitionStrategy<T extends DataSource> extends AbstractConnectionAcquisitionStrategy {

    public static final String RETRY_ATTEMPTS_HISTOGRAM = "retryAttemptsHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger( RetryConnectionAcquisitionStrategy.class);

    /**
     * The {@link RetryConnectionAcquisitionStrategy.Factory} class allows
     * creating this strategy for a given {@link ConfigurationProperties}
     */
    public static class Factory<T extends DataSource> implements ConnectionAcquisitionStrategyFactory<RetryConnectionAcquisitionStrategy, T> {
        private final int retryAttempts;

        public Factory(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        /**
         * Creates a {@link RetryConnectionAcquisitionStrategy} for a given
         * {@link ConfigurationProperties}
         *
         * @param configurationProperties configurationProperties
         * @return strategy
         */
        public RetryConnectionAcquisitionStrategy newInstance(ConfigurationProperties<T, Metrics, PoolAdapter<T>> configurationProperties) {
            return new RetryConnectionAcquisitionStrategy(
                    configurationProperties, retryAttempts
            );
        }
    }

    private final int retryAttempts;

    private final Histogram retryAttemptsHistogram;

    /**
     * Create the strategy for the given configurationProperties and the retryAttempts.
     *
     * @param configurationProperties configurationProperties
     * @param retryAttempts           maximum retry attempts
     */
    private RetryConnectionAcquisitionStrategy(ConfigurationProperties<? extends DataSource, Metrics, PoolAdapter> configurationProperties, int retryAttempts) {
        super(configurationProperties);
        this.retryAttempts = validateRetryAttempts(retryAttempts);
        this.retryAttemptsHistogram = configurationProperties.getMetrics().histogram(RETRY_ATTEMPTS_HISTOGRAM);
    }

    private int validateRetryAttempts(int retryAttempts) {
        if (retryAttempts <= 0) {
            throw new IllegalArgumentException("retryAttempts must ge greater than 0!");
        }
        return retryAttempts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(ConnectionRequestContext requestContext) throws SQLException {
        int remainingAttempts = retryAttempts;
        try {
            do {
                try {
                    return getConnectionFactory().getConnection(requestContext);
                } catch (ConnectionAcquisitionTimeoutException e) {
                    requestContext.incrementAttempts();
                    remainingAttempts--;
                    LOGGER.warn("Can't acquireConnection connection, remaining retry attempts {}", remainingAttempts);
                    if (remainingAttempts <= 0) {
                        throw e;
                    }
                }
            } while (true);
        } finally {
            int attemptedRetries = requestContext.getRetryAttempts();
            if (attemptedRetries > 0) {
                retryAttemptsHistogram.update(attemptedRetries);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RetryConnectionAcquisitionStrategy{" +
                "retryAttempts=" + retryAttempts +
                '}';
    }
}

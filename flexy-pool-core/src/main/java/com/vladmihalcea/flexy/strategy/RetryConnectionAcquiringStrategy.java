package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.builder.ConnectionAcquiringStrategyBuilder;
import com.vladmihalcea.flexy.metric.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <code>RetryConnectionAcquiringStrategy</code> extends the {@link AbstractConnectionAcquiringStrategy}
 * and it allows multiple acquiring attempts before giving up by rethrowing the {@link AcquireTimeoutException}
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public class RetryConnectionAcquiringStrategy extends AbstractConnectionAcquiringStrategy {

    public static final String RETRY_ATTEMPTS_HISTOGRAM = "retryAttemptsHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryConnectionAcquiringStrategy.class);

    /**
     * The {@link com.vladmihalcea.flexy.strategy.RetryConnectionAcquiringStrategy.Builder} class allows
     * creating this strategy for a given {@link com.vladmihalcea.flexy.config.Configuration}
     */
    public static class Builder implements ConnectionAcquiringStrategyBuilder<RetryConnectionAcquiringStrategy> {
        private final int retryAttempts;

        public Builder(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        /**
         * Build a {@link com.vladmihalcea.flexy.strategy.RetryConnectionAcquiringStrategy} for a given
         * {@link com.vladmihalcea.flexy.config.Configuration}
         * @param configuration configuration
         * @return strategy
         */
        public RetryConnectionAcquiringStrategy build(Configuration configuration) {
            return new RetryConnectionAcquiringStrategy(
                    configuration, retryAttempts
            );
        }
    }

    private final int retryAttempts;

    private final Histogram retryAttemptsHistogram;

    /**
     * Create the strategy for the given configuration and the retryAttempts.
     * @param configuration configuration
     * @param retryAttempts maximum retry attempts
     */
    private RetryConnectionAcquiringStrategy(Configuration configuration, int retryAttempts) {
        super(configuration);
        this.retryAttempts = validateRetryAttempts(retryAttempts);
        this.retryAttemptsHistogram = configuration.getMetrics().histogram(RETRY_ATTEMPTS_HISTOGRAM);
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
    public Connection getConnection(ConnectionRequestContext requestContext) throws SQLException {
        int remainingAttempts = retryAttempts;
        try {
            do {
                try {
                    return getConnectionFactory().getConnection(requestContext);
                } catch (AcquireTimeoutException e) {
                    requestContext.incrementAttempts();
                    remainingAttempts--;
                    LOGGER.info("Can't acquire connection, remaining retry attempts {}", remainingAttempts);
                    if(remainingAttempts <= 0 ) {
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
        return "RetryConnectionAcquiringStrategy{" +
                "retryAttempts=" + retryAttempts +
                '}';
    }
}

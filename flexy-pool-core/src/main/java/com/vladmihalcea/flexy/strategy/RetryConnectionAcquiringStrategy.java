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
 * RetryConnectionAcquiringStrategy - Retry pool strategy.
 *
 * @author Vlad Mihalcea
 */
public class RetryConnectionAcquiringStrategy extends AbstractConnectionAcquiringStrategy {

    public static final String RETRY_ATTEMPTS_HISTOGRAM = "retryAttemptsHistogram";

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryConnectionAcquiringStrategy.class);

    public static class Builder implements ConnectionAcquiringStrategyBuilder<RetryConnectionAcquiringStrategy> {
        private final int retryAttempts;

        public Builder(int retryAttempts) {
            this.retryAttempts = retryAttempts;
        }

        public RetryConnectionAcquiringStrategy build(Configuration configuration) {
            return new RetryConnectionAcquiringStrategy(
                    configuration, retryAttempts
            );
        }
    }

    private final int retryAttempts;

    private final Histogram retryAttemptsHistogram;

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

    @Override
    public String toString() {
        return "RetryConnectionAcquiringStrategy{" +
                "retryAttempts=" + retryAttempts +
                '}';
    }
}

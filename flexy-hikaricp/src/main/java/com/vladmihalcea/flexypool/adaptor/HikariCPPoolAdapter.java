package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * <code>HikariCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the DBCP {@link HikariDataSource}
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public class HikariCPPoolAdapter extends AbstractPoolAdapter<HikariDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Timeout of .*?ms encountered waiting for connection\\.";

    public static final PoolAdapterFactory<HikariDataSource> FACTORY = new PoolAdapterFactory<HikariDataSource>() {

        @Override
        public PoolAdapter<HikariDataSource> newInstance(
                ConfigurationProperties<HikariDataSource, Metrics, PoolAdapter<HikariDataSource>> configurationProperties) {
            return new HikariCPPoolAdapter(configurationProperties);
        }
    };

    public HikariCPPoolAdapter(ConfigurationProperties<HikariDataSource, Metrics, PoolAdapter<HikariDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaximumPoolSize();
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaximumPoolSize(maxPoolSize);
    }

    /**
     * Translate the DBCP Exception to AcquireTimeoutException.
     *
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if (e.getMessage() != null &&
                Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, e.getMessage())) {
            return new AcquireTimeoutException(e);
        }
        return new SQLException(e);
    }
}

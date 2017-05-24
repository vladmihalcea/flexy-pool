package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLTimeoutException;
import java.sql.SQLTransientConnectionException;
import java.util.regex.Pattern;

/**
 * <code>HikariCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the HikariCP {@link HikariDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class HikariCPPoolAdapter extends AbstractPoolAdapter<HikariDataSource> {

    public static final String SQL_TIMEOUT_EXCEPTION_CLASS_NAME = SQLTimeoutException.class.getName();
    
    public static final String SQL_TRANSIENT_CONNECTION_EXCEPTION_CLASS_NAME = SQLTransientConnectionException.class.getName();

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Timeout of .*?ms encountered waiting for connection\\.";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<HikariDataSource> FACTORY = new PoolAdapterFactory<HikariDataSource>() {

        @Override
        public PoolAdapter<HikariDataSource> newInstance(
                ConfigurationProperties<HikariDataSource, Metrics, PoolAdapter<HikariDataSource>> configurationProperties) {
        return new HikariCPPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     */
    public HikariCPPoolAdapter(ConfigurationProperties<HikariDataSource, Metrics, PoolAdapter<HikariDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaximumPoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaximumPoolSize(maxPoolSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isAcquireTimeoutException(Exception e) {
        return SQL_TIMEOUT_EXCEPTION_CLASS_NAME.equals(e.getClass().getName()) ||
            SQL_TRANSIENT_CONNECTION_EXCEPTION_CLASS_NAME.equals(e.getClass().getName()) ||
            (e.getMessage() != null && Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, e.getMessage()));
    }
}

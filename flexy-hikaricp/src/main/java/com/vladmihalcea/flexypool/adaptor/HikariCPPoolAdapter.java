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

    public static final String SQL_TRANSIENT_CONNECTION_EXCEPTION_CLASS_NAME = SQLTransientConnectionException.class.getName();

    public static final String ACQUISITION_TIMEOUT_MESSAGE = "Timeout of .*?ms encountered waiting for connection\\.";

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
     * @param configurationProperties configuration properties
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
        final HikariDataSource targetDataSource = getTargetDataSource();
        targetDataSource.setMaximumPoolSize(maxPoolSize);
        if (getConfigurationProperties().isMaintainFixedSizePool()) {
            targetDataSource.setMinimumIdle(maxPoolSize);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTimeoutAcquisitionException(Exception e) {
        return e instanceof SQLTimeoutException ||
            SQL_TRANSIENT_CONNECTION_EXCEPTION_CLASS_NAME.equals(e.getClass().getName()) ||
            (e.getMessage() != null && Pattern.matches( ACQUISITION_TIMEOUT_MESSAGE, e.getMessage()));
    }
}

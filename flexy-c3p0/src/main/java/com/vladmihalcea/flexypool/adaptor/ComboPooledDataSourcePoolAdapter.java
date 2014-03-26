package com.vladmihalcea.flexypool.adaptor;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import java.sql.SQLException;

/**
 * <code>ComboPooledDataSourcePoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the DBCP {@link ComboPooledDataSource}
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public class ComboPooledDataSourcePoolAdapter extends AbstractPoolAdapter<ComboPooledDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "An attempt by a client to checkout a Connection has timed out.";

    public static final PoolAdapterBuilder<ComboPooledDataSource> BUILDER = new PoolAdapterBuilder<ComboPooledDataSource>() {

        @Override
        public PoolAdapter<ComboPooledDataSource> build(
                ConfigurationProperties<ComboPooledDataSource, Metrics, PoolAdapter<ComboPooledDataSource>> configurationProperties) {
            return new ComboPooledDataSourcePoolAdapter(configurationProperties);
        }
    };

    public ComboPooledDataSourcePoolAdapter(ConfigurationProperties<ComboPooledDataSource, Metrics, PoolAdapter<ComboPooledDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxPoolSize();
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaxPoolSize(maxPoolSize);
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
                ACQUIRE_TIMEOUT_MESSAGE.equals(e.getMessage())) {
            return new AcquireTimeoutException(e);
        }
        return new SQLException(e);
    }
}

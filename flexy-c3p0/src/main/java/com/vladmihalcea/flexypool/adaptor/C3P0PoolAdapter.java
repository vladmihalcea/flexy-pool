package com.vladmihalcea.flexypool.adaptor;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

import java.sql.SQLException;

/**
 * <code>C3P0PoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the c3p0 {@link ComboPooledDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class C3P0PoolAdapter extends AbstractPoolAdapter<ComboPooledDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "An attempt by a client to checkout a Connection has timed out.";

    public static final PoolAdapterFactory<ComboPooledDataSource> FACTORY = new PoolAdapterFactory<ComboPooledDataSource>() {

        @Override
        public PoolAdapter<ComboPooledDataSource> newInstance(
                ConfigurationProperties<ComboPooledDataSource, Metrics, PoolAdapter<ComboPooledDataSource>> configurationProperties) {
            return new C3P0PoolAdapter(configurationProperties);
        }
    };

    public C3P0PoolAdapter(ConfigurationProperties<ComboPooledDataSource, Metrics, PoolAdapter<ComboPooledDataSource>> configurationProperties) {
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
     * Translate the c3p0 Exception to AcquireTimeoutException.
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
        return super.translateException(e);
    }
}

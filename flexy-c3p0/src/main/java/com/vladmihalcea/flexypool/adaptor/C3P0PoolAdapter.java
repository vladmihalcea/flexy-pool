package com.vladmihalcea.flexypool.adaptor;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

/**
 * <code>C3P0PoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the c3p0 {@link ComboPooledDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class C3P0PoolAdapter extends AbstractPoolAdapter<ComboPooledDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "An attempt by a client to checkout a Connection has timed out.";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<ComboPooledDataSource> FACTORY = new PoolAdapterFactory<ComboPooledDataSource>() {

        @Override
        public PoolAdapter<ComboPooledDataSource> newInstance(
                ConfigurationProperties<ComboPooledDataSource, Metrics, PoolAdapter<ComboPooledDataSource>> configurationProperties) {
        return new C3P0PoolAdapter(configurationProperties);
        }
    };

    /**
     * {@inheritDoc}
     */
    public C3P0PoolAdapter(ConfigurationProperties<ComboPooledDataSource, Metrics, PoolAdapter<ComboPooledDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxPoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaxPoolSize(maxPoolSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isAcquireTimeoutException(Exception e) {
        return e.getMessage() != null && ACQUIRE_TIMEOUT_MESSAGE.equals(e.getMessage());
    }
}

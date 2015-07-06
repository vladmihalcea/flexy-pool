package com.vladmihalcea.flexypool.adaptor;

import bitronix.tm.internal.BitronixRuntimeException;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

import java.util.regex.Pattern;

/**
 * <code>BitronixPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the bitronix {@link PoolingDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class BitronixPoolAdapter extends AbstractPoolAdapter<PoolingDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "XA pool of resource .*? still empty after .*?s wait time";

    public static final PoolAdapterFactory<PoolingDataSource> FACTORY = new PoolAdapterFactory<PoolingDataSource>() {

        @Override
        public PoolAdapter<PoolingDataSource> newInstance(
                ConfigurationProperties<PoolingDataSource, Metrics, PoolAdapter<PoolingDataSource>> configurationProperties) {
            return new BitronixPoolAdapter(configurationProperties);
        }
    };

    public BitronixPoolAdapter(ConfigurationProperties<PoolingDataSource, Metrics, PoolAdapter<PoolingDataSource>> configurationProperties) {
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
     * {@inheritDoc}
     */
    @Override
    protected boolean isAcquireTimeoutException(Exception e) {
        if (e.getCause() instanceof BitronixRuntimeException) {
            BitronixRuntimeException cause = (BitronixRuntimeException) e.getCause();
            return cause.getMessage() != null &&
                Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, cause.getMessage());
        }
        return false;
    }
}

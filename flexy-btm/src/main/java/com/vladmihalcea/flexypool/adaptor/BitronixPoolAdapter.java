package com.vladmihalcea.flexypool.adaptor;

import bitronix.tm.internal.BitronixRuntimeException;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * <code>BitronixPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the bitronix {@link PoolingDataSource}
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
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
     * Translate the Bitronix Exception to AcquireTimeoutException.
     *
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if (e.getCause() instanceof BitronixRuntimeException) {
            BitronixRuntimeException cause = (BitronixRuntimeException) e.getCause();
            if (cause.getMessage() != null &&
                    Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, cause.getMessage())) {
                return new AcquireTimeoutException(e);
            }
        } else if (e instanceof SQLException) {
            return (SQLException) e;
        }
        return new SQLException(e);
    }
}

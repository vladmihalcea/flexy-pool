package com.vladmihalcea.flexy.adaptor;

import bitronix.tm.internal.BitronixRuntimeException;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.builder.PoolAdapterBuilder;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * BitronixPoolAdapter - Bitronix Transaction Manager Pool Adaptor
 *
 * @author Vlad Mihalcea
 */
public class BitronixPoolAdapter extends AbstractPoolAdapter<PoolingDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "XA pool of resource .*? still empty after .*?s wait time";

    public static final PoolAdapterBuilder<PoolingDataSource> BUILDER = new PoolAdapterBuilder<PoolingDataSource>() {
        @Override
        public PoolAdapter<PoolingDataSource> build(Configuration<PoolingDataSource> configuration) {
            return new BitronixPoolAdapter(configuration);
        }
    };

    public BitronixPoolAdapter(Configuration<PoolingDataSource> configuration) {
        super(configuration);
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
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if(e.getCause() instanceof BitronixRuntimeException) {
            BitronixRuntimeException cause = (BitronixRuntimeException) e.getCause();
            if(cause.getMessage() != null &&
                    Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, cause.getMessage())) {
                return new AcquireTimeoutException(e);
            }
        } else if(e instanceof SQLException) {
            return (SQLException) e;
        }
        return new SQLException(e);
    }
}

package com.vladmihalcea.flexy.adaptor;

import bitronix.tm.internal.BitronixRuntimeException;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.metric.Metrics;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * BitronixPoolAdapter - Bitronix Transaction Manager Pool Adaptor
 *
 * @author Vlad Mihalcea
 */
public class BitronixPoolAdapter extends AbstractPoolAdapter<PoolingDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "XA pool of resource .*? still empty after .*?s wait time";

    public BitronixPoolAdapter(Metrics metrics, PoolingDataSource dataSource) {
        super(metrics, dataSource);
    }

    @Override
    public int getMaxPoolSize() {
        return getPoolingDataSource().getMaxPoolSize();
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getPoolingDataSource().setMaxPoolSize(maxPoolSize);
    }

    @Override
    public int getMinPoolSize() {
        return getPoolingDataSource().getMinPoolSize();
    }

    @Override
    public void setMinPoolSize(int minPoolSize) {
        getPoolingDataSource().setMinPoolSize(minPoolSize);
    }

    /**
     * Translate the Bitronix Exception to AcquireTimeoutException.
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException launderSQLException(SQLException e) {
        if(e.getCause() instanceof BitronixRuntimeException) {
            BitronixRuntimeException cause = (BitronixRuntimeException) e.getCause();
            if(cause.getMessage() != null &&
                    Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, cause.getMessage())) {
                return new AcquireTimeoutException(e);
            }
        }
        return e;
    }
}

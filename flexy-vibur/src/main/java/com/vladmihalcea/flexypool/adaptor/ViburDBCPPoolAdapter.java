package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.vibur.dbcp.ViburDBCPDataSource;

import java.sql.SQLException;

/**
 * <code>ViburDBCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Vubur DBCP {@link ViburDBCPDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.2.1
 */
public class ViburDBCPPoolAdapter extends AbstractPoolAdapter<ViburDBCPDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Couldn't obtain SQL connection from pool";

    public static final PoolAdapterFactory<ViburDBCPDataSource> FACTORY = new PoolAdapterFactory<ViburDBCPDataSource>() {

        @Override
        public PoolAdapter<ViburDBCPDataSource> newInstance(
                ConfigurationProperties<ViburDBCPDataSource, Metrics, PoolAdapter<ViburDBCPDataSource>> configurationProperties) {
            return new ViburDBCPPoolAdapter(configurationProperties);
        }
    };

    public ViburDBCPPoolAdapter(ConfigurationProperties<ViburDBCPDataSource, Metrics, PoolAdapter<ViburDBCPDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getPoolMaxSize();
    }

    /**
     * Vibur DBCP does not support pool resizing natively, as C3P0.
     * This way, it's impossible to guarantee what will happen to the current acquired connections one the pool
     * has to be destroyed and recreated, only to take into consideration the new pool size.
     * Therefore, the safest approach is to throw an UnsupportedOperationException
     * whenever the max pool size is about to be changed and document the behavior.
     *
     * @param maxPoolSize the upper amount of pooled connections.
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        throw new UnsupportedOperationException("Vibur DBCP doesn't reinitialize itself on pool size change");
    }

    /**
     * Translate the Vibur DBCP Exception to AcquireTimeoutException.
     *
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if (e.getMessage() != null &&
                e.getMessage().startsWith(ACQUIRE_TIMEOUT_MESSAGE)) {
            return new AcquireTimeoutException(e);
        }
        return new SQLException(e);
    }
}

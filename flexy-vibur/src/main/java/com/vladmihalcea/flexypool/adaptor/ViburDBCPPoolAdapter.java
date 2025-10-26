package com.vladmihalcea.flexypool.adaptor;

import java.sql.SQLTimeoutException;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;
import org.vibur.dbcp.ViburDBCPDataSource;

/**
 * <code>ViburDBCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Vubur DBCP {@link ViburDBCPDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.2.1
 */
public class ViburDBCPPoolAdapter extends AbstractPoolAdapter<ViburDBCPDataSource> {

    public static final String ACQUISITION_TIMEOUT_MESSAGE = "Couldn't obtain SQL connection from pool";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<ViburDBCPDataSource> FACTORY = new PoolAdapterFactory<ViburDBCPDataSource>() {

        @Override
        public PoolAdapter<ViburDBCPDataSource> newInstance(
                ConfigurationProperties<ViburDBCPDataSource, Metrics, PoolAdapter<ViburDBCPDataSource>> configurationProperties) {
            return new ViburDBCPPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     */
    public ViburDBCPPoolAdapter(ConfigurationProperties<ViburDBCPDataSource, Metrics, PoolAdapter<ViburDBCPDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
     */
    @Override
    protected boolean isTimeoutAcquisitionException(Exception e) {
        return e instanceof SQLTimeoutException ||
                (e.getMessage() != null && e.getMessage().startsWith( ACQUISITION_TIMEOUT_MESSAGE ));
    }
}

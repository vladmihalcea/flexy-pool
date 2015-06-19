package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.vibur.dbcp.DataSourceLifecycle;
import org.vibur.dbcp.ViburDBCPDataSource;

import java.sql.SQLException;

/**
 * <code>DBCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Vubur DBCP {@link ViburDBCPDataSource}
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
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

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setPoolMaxSize(maxPoolSize);
        ReflectionUtils.setFieldValue(getTargetDataSource(), "state", DataSourceLifecycle.State.NEW);
        boolean enableJMX = getTargetDataSource().isEnableJMX();
        getTargetDataSource().setEnableJMX(false);
        getTargetDataSource().start();
        if (enableJMX) {
            getTargetDataSource().setEnableJMX(true);
        }
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
                e.getMessage().startsWith(ACQUIRE_TIMEOUT_MESSAGE)) {
            return new AcquireTimeoutException(e);
        }
        return new SQLException(e);
    }
}

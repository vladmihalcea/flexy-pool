package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolExhaustedException;

import java.sql.SQLException;

/**
 * <code>TomcatCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Tomcat CP {@link DataSource}
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public class TomcatCPPoolAdapter extends AbstractPoolAdapter<DataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Timeout of .*?ms encountered waiting for connection\\.";

    public static final PoolAdapterFactory<DataSource> FACTORY = new PoolAdapterFactory<DataSource>() {

        @Override
        public PoolAdapter<DataSource> newInstance(
                ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
            return new TomcatCPPoolAdapter(configurationProperties);
        }
    };

    public TomcatCPPoolAdapter(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
        super(configurationProperties);
    }

    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxActive();
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaxActive(maxPoolSize);
    }

    /**
     * Translate the TomcatCP Exception to AcquireTimeoutException.
     *
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if (e instanceof PoolExhaustedException) {
            return new AcquireTimeoutException(e);
        }
        return new SQLException(e);
    }
}

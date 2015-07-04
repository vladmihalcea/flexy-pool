package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * <code>DBCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the DBCP {@link BasicDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class DBCPPoolAdapter extends AbstractPoolAdapter<BasicDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Cannot get a connection, pool error Timeout waiting for idle object";

    public static final PoolAdapterFactory<BasicDataSource> FACTORY = new PoolAdapterFactory<BasicDataSource>() {

        @Override
        public PoolAdapter<BasicDataSource> newInstance(
                ConfigurationProperties<BasicDataSource, Metrics, PoolAdapter<BasicDataSource>> configurationProperties) {
            return new DBCPPoolAdapter(configurationProperties);
        }
    };

    public DBCPPoolAdapter(ConfigurationProperties<BasicDataSource, Metrics, PoolAdapter<BasicDataSource>> configurationProperties) {
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
     * Translate the DBCP Exception to AcquireTimeoutException.
     *
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if (e.getMessage() != null &&
                Pattern.matches(ACQUIRE_TIMEOUT_MESSAGE, e.getMessage())) {
            return new AcquireTimeoutException(e);
        }
        return new SQLException(e);
    }
}

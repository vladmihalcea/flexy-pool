package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;
import org.apache.commons.dbcp.BasicDataSource;

import java.util.regex.Pattern;

/**
 * <code>DBCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the DBCP {@link BasicDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class DBCPPoolAdapter extends AbstractPoolAdapter<BasicDataSource> {

    public static final String ACQUISITION_TIMEOUT_MESSAGE = "Cannot get a connection, pool error Timeout waiting for idle object";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<BasicDataSource> FACTORY = new PoolAdapterFactory<BasicDataSource>() {

        @Override
        public PoolAdapter<BasicDataSource> newInstance(
                ConfigurationProperties<BasicDataSource, Metrics, PoolAdapter<BasicDataSource>> configurationProperties) {
            return new DBCPPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     */
    public DBCPPoolAdapter(ConfigurationProperties<BasicDataSource, Metrics, PoolAdapter<BasicDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        final BasicDataSource targetDataSource = getTargetDataSource();
        targetDataSource.setMaxActive(maxPoolSize);
        if (getConfigurationProperties().isMaintainFixedSizePool()) {
            targetDataSource.setMinIdle(maxPoolSize);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTimeoutAcquisitionException(Exception e) {
        return e.getMessage() != null && Pattern.matches( ACQUISITION_TIMEOUT_MESSAGE, e.getMessage());
    }
}

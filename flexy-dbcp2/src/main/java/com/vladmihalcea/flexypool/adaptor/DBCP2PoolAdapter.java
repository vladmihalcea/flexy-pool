package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * <code>DBCP2PoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the DBCP2 {@link BasicDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class DBCP2PoolAdapter extends AbstractPoolAdapter<BasicDataSource> {

    public static final String ACQUISITION_TIMEOUT_MESSAGE = "Cannot get a connection, pool error Timeout waiting for idle object";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<BasicDataSource> FACTORY = new PoolAdapterFactory<BasicDataSource>() {

        @Override
        public PoolAdapter<BasicDataSource> newInstance(
                ConfigurationProperties<BasicDataSource, Metrics, PoolAdapter<BasicDataSource>> configurationProperties) {
        return new DBCP2PoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     */
    public DBCP2PoolAdapter(ConfigurationProperties<BasicDataSource, Metrics, PoolAdapter<BasicDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxTotal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaxTotal(maxPoolSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTimeoutAcquisitionException(Exception e) {
        return e.getMessage() != null && ACQUISITION_TIMEOUT_MESSAGE.equals( e.getMessage());
    }
}

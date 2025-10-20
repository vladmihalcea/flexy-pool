package com.vladmihalcea.flexypool.adaptor;

import com.alibaba.druid.TransactionTimeoutException;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

/**
 * <code>DruidAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Druid {@link DruidDataSource}
 *
 * @author Weapon Lin
 */
public class DruidAdapter extends AbstractPoolAdapter<DruidDataSource> {

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<DruidDataSource> FACTORY = new PoolAdapterFactory<DruidDataSource>() {
        @Override
        public PoolAdapter<DruidDataSource> newInstance(ConfigurationProperties<DruidDataSource, Metrics, PoolAdapter<DruidDataSource>> configurationProperties) {
            return new DruidAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     */
    public DruidAdapter(ConfigurationProperties<DruidDataSource, Metrics, PoolAdapter<DruidDataSource>> configurationProperties) {
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
        final DruidDataSource targetDataSource = getTargetDataSource();
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
        return e.getClass() == GetConnectionTimeoutException.class
                || e.getClass() == TransactionTimeoutException.class;
    }
}

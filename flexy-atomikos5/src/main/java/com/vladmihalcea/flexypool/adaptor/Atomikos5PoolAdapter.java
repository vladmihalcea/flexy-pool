package com.vladmihalcea.flexypool.adaptor;

import com.atomikos.jdbc.internal.AbstractDataSourceBean;
import com.atomikos.jdbc.internal.AtomikosSQLException;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

/**
 * <code>Atomikos5PoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Atomikos 5 {@link AbstractDataSourceBean}.
 *
 * @author Vlad Mihalcea
 * @since 2.2.0
 */
public class Atomikos5PoolAdapter extends AbstractPoolAdapter<AbstractDataSourceBean> {

    public static final String ACQUISITION_TIMEOUT_MESSAGE = "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the DataSourceBean.";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<AbstractDataSourceBean> FACTORY = new PoolAdapterFactory<AbstractDataSourceBean>() {

        @Override
        public PoolAdapter<AbstractDataSourceBean> newInstance(
            ConfigurationProperties<AbstractDataSourceBean, Metrics, PoolAdapter<AbstractDataSourceBean>> configurationProperties) {
            return new Atomikos5PoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     */
    public Atomikos5PoolAdapter(ConfigurationProperties<AbstractDataSourceBean, Metrics, PoolAdapter<AbstractDataSourceBean>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxPoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaxPoolSize(maxPoolSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTimeoutAcquisitionException(Exception e) {
        if (e instanceof AtomikosSQLException) {
            return e.getMessage() != null &&
                ACQUISITION_TIMEOUT_MESSAGE.equals( e.getMessage());
        }
        return false;
    }
}

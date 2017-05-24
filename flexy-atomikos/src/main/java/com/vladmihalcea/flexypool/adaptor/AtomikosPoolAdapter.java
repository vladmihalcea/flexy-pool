package com.vladmihalcea.flexypool.adaptor;

import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

/**
 * <code>AtomikosPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the {@link AbstractDataSourceBean Atomikos DataSourceBean}
 *
 * @author Vlad Mihalcea
 * @since 1.2.1
 */
public class AtomikosPoolAdapter extends AbstractPoolAdapter<AbstractDataSourceBean> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the DataSourceBean.";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<AbstractDataSourceBean> FACTORY = new PoolAdapterFactory<AbstractDataSourceBean>() {

        @Override
        public PoolAdapter<AbstractDataSourceBean> newInstance(
                ConfigurationProperties<AbstractDataSourceBean, Metrics, PoolAdapter<AbstractDataSourceBean>> configurationProperties) {
            return new AtomikosPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     */
    public AtomikosPoolAdapter(ConfigurationProperties<AbstractDataSourceBean, Metrics, PoolAdapter<AbstractDataSourceBean>> configurationProperties) {
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
    protected boolean isAcquireTimeoutException(Exception e) {
        if (e instanceof AtomikosSQLException) {
            AtomikosSQLException atomikosSQLException = (AtomikosSQLException) e;
            return atomikosSQLException.getMessage() != null &&
                ACQUIRE_TIMEOUT_MESSAGE.equals(atomikosSQLException.getMessage());
        }
        return false;
    }
}

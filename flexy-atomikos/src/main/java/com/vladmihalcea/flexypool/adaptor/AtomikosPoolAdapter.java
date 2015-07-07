package com.vladmihalcea.flexypool.adaptor;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

/**
 * <code>AtomikosPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the {@link AtomikosDataSourceBean}
 *
 * @author Vlad Mihalcea
 * @since 1.2.1
 */
public class AtomikosPoolAdapter extends AbstractPoolAdapter<AtomikosDataSourceBean> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the DataSourceBean.";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<AtomikosDataSourceBean> FACTORY = new PoolAdapterFactory<AtomikosDataSourceBean>() {

        @Override
        public PoolAdapter<AtomikosDataSourceBean> newInstance(
                ConfigurationProperties<AtomikosDataSourceBean, Metrics, PoolAdapter<AtomikosDataSourceBean>> configurationProperties) {
            return new AtomikosPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     */
    public AtomikosPoolAdapter(ConfigurationProperties<AtomikosDataSourceBean, Metrics, PoolAdapter<AtomikosDataSourceBean>> configurationProperties) {
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

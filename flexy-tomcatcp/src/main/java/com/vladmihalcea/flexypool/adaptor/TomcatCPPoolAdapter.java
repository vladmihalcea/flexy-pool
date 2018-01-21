package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolExhaustedException;

/**
 * <code>TomcatCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Tomcat CP {@link DataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.1.0
 */
public class TomcatCPPoolAdapter extends AbstractPoolAdapter<DataSource> {

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<DataSource> FACTORY = new PoolAdapterFactory<DataSource>() {

        @Override
        public PoolAdapter<DataSource> newInstance(
                ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
            return new TomcatCPPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     * @param configurationProperties configuration properties
     */
    public TomcatCPPoolAdapter(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
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
        getTargetDataSource().setMaxActive(maxPoolSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isAcquireTimeoutException(Exception e) {
        return e instanceof PoolExhaustedException;
    }
}

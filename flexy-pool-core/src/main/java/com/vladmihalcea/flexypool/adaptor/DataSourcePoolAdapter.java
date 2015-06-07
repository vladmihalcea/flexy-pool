package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * <code>DataSourcePoolAdapter</code> extends the {@link AbstractPoolAdapter} class and it works with any {@link DataSource}
 *
 * Because it's a generic PoolAdapter, it cannot read or write the pool size, since the DataSource might not even be
 * a connection pool.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 * @see com.vladmihalcea.flexypool.adaptor.PoolAdapter
 */
public class DataSourcePoolAdapter extends AbstractPoolAdapter<DataSource> {

    public static final PoolAdapterFactory<DataSource> FACTORY = new PoolAdapterFactory<DataSource>() {

        @Override
        public PoolAdapter<DataSource> newInstance(
                ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
        return new DataSourcePoolAdapter(configurationProperties);
        }
    };

    public DataSourcePoolAdapter(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
        super(configurationProperties);
    }

    @Override
    public int getMaxPoolSize() {
        throw new UnsupportedOperationException("The DataSourcePoolAdapter cannot read the max pool size");
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        throw new UnsupportedOperationException("The DataSourcePoolAdapter cannot write the max pool size");
    }
}

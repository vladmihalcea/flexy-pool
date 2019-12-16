package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ReflectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * <code>AtomikosPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Atomikos 5 or 4 {@code AbstractDataSourceBean}.
 *
 * @author Vlad Mihalcea
 * @since 1.2.1
 */
public class AtomikosPoolAdapter implements PoolAdapter<DataSource> {

    private AbstractPoolAdapter poolAdapter;

    private DataSource targetDataSource;

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<DataSource> FACTORY = new PoolAdapterFactory<DataSource>() {

        @Override
        public PoolAdapter<DataSource> newInstance(
                ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
            return new AtomikosPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     */
    @SuppressWarnings("unchecked")
    public AtomikosPoolAdapter(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
        this.targetDataSource = configurationProperties.getTargetDataSource();

        ConfigurationProperties atmomikosConfigurationProperties = configurationProperties;

        Class atomikos5DataSourceClass = ReflectionUtils.getClassOrNull("com.atomikos.jdbc.internal.AbstractDataSourceBean");

        if(atomikos5DataSourceClass != null && atomikos5DataSourceClass.isInstance(this.targetDataSource)) {
            poolAdapter = new Atomikos5PoolAdapter(atmomikosConfigurationProperties);
        } else {
            Class atomikos4DataSourceClass = ReflectionUtils.getClassOrNull("com.atomikos.jdbc.AbstractDataSourceBean");
            if (atomikos4DataSourceClass != null && atomikos4DataSourceClass.isInstance(this.targetDataSource)) {
                poolAdapter = new Atomikos4PoolAdapter(atmomikosConfigurationProperties);
            } else {
                throw new UnsupportedOperationException("The provided DataSource [" + targetDataSource + "] is not an instance of the AbstractDataSourceBean class from either Atomikos 5 or 4");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(ConnectionRequestContext requestContext) throws SQLException {
        return poolAdapter.getConnection(requestContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getTargetDataSource() {
        return targetDataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return poolAdapter.getMaxPoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        poolAdapter.setMaxPoolSize(maxPoolSize);
    }
}

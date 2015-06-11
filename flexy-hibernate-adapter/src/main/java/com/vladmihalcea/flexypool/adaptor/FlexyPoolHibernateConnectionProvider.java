package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * FlexyPoolConnectionProvider - This is the Hibernate custom DataSource adapter.
 * It allows registering the {@link com.vladmihalcea.flexypool.FlexyPoolDataSource} instead of the original
 * {@link javax.sql.DataSource} defined in the persistence.xml descriptor.
 *
 * @author Vlad Mihalcea
 */
public class FlexyPoolHibernateConnectionProvider extends DatasourceConnectionProviderImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexyPoolHibernateConnectionProvider.class);

    private FlexyPoolDataSource<DataSource> flexyPoolDataSource;

    /**
     * Substitute the already configured {@link DataSource} with the {@link FlexyPoolDataSource}
     * @param props
     * @throws HibernateException
     */
    @Override
    public void configure(Map props) throws HibernateException {
        super.configure(props);
        LOGGER.debug("Hibernate switched to using FlexyPoolDataSource");
        flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(getDataSource());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        return this.flexyPoolDataSource.getConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean isUnwrappableAs(Class unwrapType) {
        return super.isUnwrappableAs(unwrapType) || FlexyPoolHibernateConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        this.flexyPoolDataSource.stop();
        super.stop();
    }
}

package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.connection.ConnectionFactory;

import javax.sql.DataSource;

/**
 * PoolAdapter - Adaptor for external Connection Pools.
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapter extends ConnectionFactory {

    /**
     * Wrapped connection pool data source
     * @return connection pool data source
     */
    DataSource getDataSource();

    /**
     * @return the max amount of pooled connections.
     */
    int getMaxPoolSize();

    /**
     * Define the max amount of pooled connections.
     * @param maxPoolSize the upper amount of pooled connections.
     */
    void setMaxPoolSize(int maxPoolSize);

    /**
     * @return the min amount of pooled connections.
     */
    int getMinPoolSize();

    /**
     * Define the min amount of pooled connections.
     * @param minPoolSize the lower amount of pooled connections.
     */
    void setMinPoolSize(int minPoolSize);
}

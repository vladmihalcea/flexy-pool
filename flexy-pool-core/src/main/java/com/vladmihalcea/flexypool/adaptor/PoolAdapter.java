package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.connection.ConnectionFactory;

import javax.sql.DataSource;

/**
 * <code>PoolAdapter</code> provides an abstraction layer for external connection pools.
 * <p>
 * <p>Every supported connection pool must have an associated PoolAdapter defining methods for:
 * <p>
 * <ul>
 * <li>retrieving the pool max size {@link com.vladmihalcea.flexypool.adaptor.PoolAdapter#getMaxPoolSize()}
 * <p>
 * <li>setting the pool max size {@link com.vladmihalcea.flexypool.adaptor.PoolAdapter#setMaxPoolSize(int maxPoolSize)} ()}
 * <p>
 * <li>retrieving the pool data source
 *
 * @author Vlad Mihalcea
 * @see com.vladmihalcea.flexypool.connection.ConnectionFactory
 * @since 1.0
 */
public interface PoolAdapter<T extends DataSource> extends ConnectionFactory {

    /**
     * Associated connection pool data source
     *
     * @return connection pool target data source
     */
    T getTargetDataSource();

    /**
     * @return the max amount of pooled connections.
     */
    int getMaxPoolSize();

    /**
     * Define the max amount of pooled connections.
     *
     * @param maxPoolSize the upper amount of pooled connections.
     */
    void setMaxPoolSize(int maxPoolSize);
}

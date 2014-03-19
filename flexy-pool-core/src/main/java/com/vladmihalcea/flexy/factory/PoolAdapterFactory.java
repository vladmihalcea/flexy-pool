package com.vladmihalcea.flexy.factory;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.config.Configuration;

import javax.sql.DataSource;

/**
 * PoolAdapterFactory - Pool Adapter Configuration based factory
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapterFactory<T extends DataSource> {

    PoolAdapter<T> newInstance(Configuration<T> configuration);
}

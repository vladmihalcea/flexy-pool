package com.vladmihalcea.flexy.config.builder;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.config.Configuration;

import javax.sql.DataSource;

/**
 * PoolAdapterBuilder - Pool Adapter Configuration based builder
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapterBuilder<T extends DataSource> {

    PoolAdapter<T> build(Configuration<T> configuration);
}

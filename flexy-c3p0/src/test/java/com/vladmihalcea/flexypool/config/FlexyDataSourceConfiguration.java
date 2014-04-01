package com.vladmihalcea.flexypool.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vladmihalcea.flexypool.adaptor.ComboPooledDataSourcePoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyDataSourceConfiguration extends AbstractFlexyDataSourceConfiguration<ComboPooledDataSource> {

    @Autowired
    private ComboPooledDataSource poolingDataSource;

    @Override
    public ComboPooledDataSource getPoolingDataSource() {
        return poolingDataSource;
    }

    @Bean
    public Configuration configuration() {
        return configuration(ComboPooledDataSourcePoolAdapter.FACTORY);
    }
}

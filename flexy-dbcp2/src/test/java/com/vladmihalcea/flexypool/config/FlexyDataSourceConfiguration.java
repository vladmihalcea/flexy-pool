package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.BasicDataSourcePoolAdapter;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyDataSourceConfiguration extends AbstractFlexyDataSourceConfiguration<BasicDataSource> {

    @Autowired
    BasicDataSource poolingDataSource;

    @Override
    public BasicDataSource getPoolingDataSource() {
        return poolingDataSource;
    }

    @Bean
    public Configuration configuration() {
        return configuration(BasicDataSourcePoolAdapter.BUILDER);
    }
}

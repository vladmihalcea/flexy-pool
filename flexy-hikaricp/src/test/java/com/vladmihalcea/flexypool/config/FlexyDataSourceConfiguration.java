package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.HikariDataSourcePoolAdapter;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyDataSourceConfiguration extends AbstractFlexyDataSourceConfiguration<HikariDataSource> {

    @Autowired
    HikariDataSource poolingDataSource;

    @Override
    public HikariDataSource getPoolingDataSource() {
        return poolingDataSource;
    }

    @Bean
    public Configuration configuration() {
        return configuration(HikariDataSourcePoolAdapter.FACTORY);
    }
}

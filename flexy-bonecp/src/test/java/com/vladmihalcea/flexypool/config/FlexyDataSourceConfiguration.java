package com.vladmihalcea.flexypool.config;

import com.jolbox.bonecp.BoneCPDataSource;
import com.vladmihalcea.flexypool.adaptor.BoneCPDataSourcePoolAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyDataSourceConfiguration extends AbstractFlexyDataSourceConfiguration<BoneCPDataSource> {

    @Autowired
    BoneCPDataSource poolingDataSource;

    @Override
    public BoneCPDataSource getPoolingDataSource() {
        return poolingDataSource;
    }

    @Bean
    public Configuration configuration() {
        return configuration(BoneCPDataSourcePoolAdapter.FACTORY);
    }
}

package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.ViburDBCPPoolAdapter;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquisitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.vibur.dbcp.ViburDBCPDataSource;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolBeanConfiguration {

    @Autowired
    private ViburDBCPDataSource poolingDataSource;

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;

    @Bean
    public FlexyPoolConfiguration<ViburDBCPDataSource> configuration() {
        return new FlexyPoolConfiguration.Builder<ViburDBCPDataSource>(
                uniqueId,
                poolingDataSource,
                ViburDBCPPoolAdapter.FACTORY
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        FlexyPoolConfiguration<ViburDBCPDataSource> configuration = configuration();
        return new FlexyPoolDataSource<ViburDBCPDataSource>(configuration,
                new RetryConnectionAcquisitionStrategy.Factory( 2)
        );
    }
}

package com.vladmihalcea.flexypool.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.C3P0PoolAdapter;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquisitionStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquisitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * FlexyPoolConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolBeanConfiguration {

    @Autowired
    private ComboPooledDataSource poolingDataSource;

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;

    @Bean
    public FlexyPoolConfiguration<ComboPooledDataSource> configuration() {
        return new FlexyPoolConfiguration.Builder<ComboPooledDataSource>(
                uniqueId,
                poolingDataSource,
                C3P0PoolAdapter.FACTORY
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        FlexyPoolConfiguration<ComboPooledDataSource> configuration = configuration();
        return new FlexyPoolDataSource<ComboPooledDataSource>(configuration,
                new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory( 5),
                new RetryConnectionAcquisitionStrategy.Factory( 2)
        );
    }
}

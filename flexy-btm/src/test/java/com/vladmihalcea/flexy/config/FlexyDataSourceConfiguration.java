package com.vladmihalcea.flexy.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexy.FlexyPoolDataSource;
import com.vladmihalcea.flexy.adaptor.BitronixPoolAdapter;
import com.vladmihalcea.flexy.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexy.strategy.RetryConnectionAcquiringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FlexyDataSourceConfiguration - Configuration for flexy data source
 *
 * @author Vlad Mihalcea
 */
@Configuration
public class FlexyDataSourceConfiguration {

    @Autowired
    private PoolingDataSource poolingDataSource;

    @Bean
    public BitronixPoolAdapter bitronixPoolAdaptor() {
        return new BitronixPoolAdapter(poolingDataSource);
    }

    @Bean
    public FlexyPoolDataSource dataSource() {
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy =
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy(bitronixPoolAdaptor(), 5);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy(
                incrementPoolOnTimeoutConnectionAcquiringStrategy, 2
        );
        return new FlexyPoolDataSource(retryConnectionAcquiringStrategy);
    }
}

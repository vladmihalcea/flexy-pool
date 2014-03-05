package com.vladmihalcea.flexy.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexy.FlexyPoolDataSource;
import com.vladmihalcea.flexy.adaptor.BitronixPoolAdapter;
import com.vladmihalcea.flexy.context.Context;
import com.vladmihalcea.flexy.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexy.strategy.RetryConnectionAcquiringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * FlexyDataSourceConfiguration - Configuration for flexy data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyDataSourceConfiguration {

    @Autowired
    private PoolingDataSource poolingDataSource;

    @Bean
    public BitronixPoolAdapter bitronixPoolAdaptor() {
        return new BitronixPoolAdapter(poolingDataSource);
    }

    @Bean
    public FlexyPoolDataSource dataSource() {
        Configuration configuration = new Configuration(UUID.randomUUID().toString());
        Context context = new Context(configuration);
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy =
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy(context, bitronixPoolAdaptor(), 5);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy(
                context, incrementPoolOnTimeoutConnectionAcquiringStrategy, 2
        );
        return new FlexyPoolDataSource(context, retryConnectionAcquiringStrategy);
    }
}

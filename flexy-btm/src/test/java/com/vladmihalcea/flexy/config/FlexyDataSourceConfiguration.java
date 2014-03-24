package com.vladmihalcea.flexy.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexy.FlexyPoolDataSource;
import com.vladmihalcea.flexy.adaptor.BitronixPoolAdapter;
import com.vladmihalcea.flexy.metric.codahale.CodahaleMetrics;
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
    public Configuration configuration() {
        return new Configuration.Builder<PoolingDataSource>(
                UUID.randomUUID().toString(),
                poolingDataSource,
                CodahaleMetrics.BUILDER,
                BitronixPoolAdapter.BUILDER
        ).build();
    }

    @Bean
    public FlexyPoolDataSource dataSource() {
        Configuration configuration = configuration();
        return new FlexyPoolDataSource(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder(5),
                new RetryConnectionAcquiringStrategy.Builder(2)
        );
    }
}

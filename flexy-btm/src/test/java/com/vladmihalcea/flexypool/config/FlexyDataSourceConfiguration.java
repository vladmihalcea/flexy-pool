package com.vladmihalcea.flexypool.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.BitronixPoolAdapter;
import com.vladmihalcea.flexypool.metric.codahale.CodahaleMetrics;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
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

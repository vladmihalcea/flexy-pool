package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.BasicDataSourcePoolAdapter;
import com.vladmihalcea.flexypool.metric.codahale.CodahaleMetrics;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import org.apache.commons.dbcp.BasicDataSource;
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
    private BasicDataSource basicDataSource;

    @Bean
    public Configuration configuration() {
        return new Configuration.Builder<BasicDataSource>(
                UUID.randomUUID().toString(),
                basicDataSource,
                CodahaleMetrics.BUILDER,
                BasicDataSourcePoolAdapter.BUILDER
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        Configuration configuration = configuration();
        return new FlexyPoolDataSource(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder(5),
                new RetryConnectionAcquiringStrategy.Builder(2)
        );
    }
}

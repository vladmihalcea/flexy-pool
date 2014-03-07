package com.vladmihalcea.flexy.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexy.FlexyPoolDataSource;
import com.vladmihalcea.flexy.adaptor.BitronixPoolAdapter;
import com.vladmihalcea.flexy.context.Context;
import com.vladmihalcea.flexy.metric.Metrics;
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
        return new Configuration(UUID.randomUUID().toString());
    }

    @Bean
    public Metrics metrics() {
        return new CodahaleMetrics(configuration(), Metrics.class);
    }

    @Bean
    public BitronixPoolAdapter bitronixPoolAdaptor() {
        return new BitronixPoolAdapter(metrics(), poolingDataSource);
    }

    @Bean
    public Context context() {
        return new Context(configuration(), metrics(), bitronixPoolAdaptor());
    }

    @Bean
    public FlexyPoolDataSource dataSource() {
        Context context = context();
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy =
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy(context, 5);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy(
                context, incrementPoolOnTimeoutConnectionAcquiringStrategy, 2
        );
        return new FlexyPoolDataSource(context, retryConnectionAcquiringStrategy);
    }
}

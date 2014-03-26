package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterBuilder;
import com.vladmihalcea.flexypool.metric.codahale.CodahaleMetrics;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * AbstractFlexyDataSourceConfiguration - Abstract Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractFlexyDataSourceConfiguration<T extends DataSource> {

    public abstract T getPoolingDataSource();

    public abstract Configuration configuration();

    public int getMaxOverflowPoolSize() {
        return 5;
    }

    public int getRetryAttempts() {
        return 2;
    }

    protected Configuration configuration(PoolAdapterBuilder<T> poolAdapter) {
        return new Configuration.Builder<T>(
                UUID.randomUUID().toString(),
                getPoolingDataSource(),
                CodahaleMetrics.BUILDER,
                poolAdapter
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        Configuration configuration = configuration();
        return new FlexyPoolDataSource(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder(getMaxOverflowPoolSize()),
                new RetryConnectionAcquiringStrategy.Builder(getRetryAttempts())
        );
    }
}

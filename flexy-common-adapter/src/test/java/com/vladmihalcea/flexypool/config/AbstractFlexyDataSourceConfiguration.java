package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
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

    protected Configuration configuration(PoolAdapterFactory<T> poolAdapter) {
        return new Configuration.Builder<T>(
                UUID.randomUUID().toString(),
                getPoolingDataSource(),
                poolAdapter
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        Configuration configuration = configuration();
        return new FlexyPoolDataSource(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory(getMaxOverflowPoolSize()),
                new RetryConnectionAcquiringStrategy.Factory(getRetryAttempts())
        );
    }
}

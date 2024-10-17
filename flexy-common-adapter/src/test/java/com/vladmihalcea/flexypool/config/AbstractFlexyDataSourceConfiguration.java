package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquisitionStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquisitionStrategy;
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

    public abstract FlexyPoolConfiguration configuration();

    public int getMaxOverflowPoolSize() {
        return 5;
    }

    public int getRetryAttempts() {
        return 2;
    }

    protected FlexyPoolConfiguration configuration(PoolAdapterFactory<T> poolAdapter) {
        return new FlexyPoolConfiguration.Builder<T>(
                UUID.randomUUID().toString(),
                getPoolingDataSource(),
                poolAdapter
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        FlexyPoolConfiguration configuration = configuration();
        return new FlexyPoolDataSource(configuration,
                new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory( getMaxOverflowPoolSize()),
                new RetryConnectionAcquisitionStrategy.Factory( getRetryAttempts())
        );
    }
}

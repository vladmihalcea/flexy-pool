package com.vladmihalcea.flexypool.config;

import java.util.concurrent.TimeUnit;

import com.atomikos.jdbc.AbstractDataSourceBean;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.Atomikos4PoolAdapter;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * FlexyDataSourceConfiguration - Configuration for FlexyPool data source
 *
 * @author Vlad Mihalcea
 * @since 2.2.0
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolConfiguration {

    @Autowired
    private AbstractDataSourceBean poolingDataSource;

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;

    @Bean
    public Configuration<AbstractDataSourceBean> configuration() {
        return new Configuration.Builder<AbstractDataSourceBean>(
                uniqueId,
                poolingDataSource,
                Atomikos4PoolAdapter.FACTORY
        )
        .setJmxEnabled(true)
        .setMetricLogReporterMillis(TimeUnit.SECONDS.toMillis(5))
        .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        Configuration<AbstractDataSourceBean> configuration = configuration();
        return new FlexyPoolDataSource<AbstractDataSourceBean>(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory(5),
                new RetryConnectionAcquiringStrategy.Factory(2)
        );
    }
}

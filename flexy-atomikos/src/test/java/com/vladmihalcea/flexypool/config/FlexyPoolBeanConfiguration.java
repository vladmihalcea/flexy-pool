package com.vladmihalcea.flexypool.config;

import java.util.concurrent.TimeUnit;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.AtomikosPoolAdapter;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquisitionStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquisitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolBeanConfiguration {

    @Autowired
    private DataSource poolingDataSource;

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;

    @Bean
    public FlexyPoolConfiguration<DataSource> configuration() {
        return new FlexyPoolConfiguration.Builder<DataSource>(
                uniqueId,
                poolingDataSource,
                AtomikosPoolAdapter.FACTORY
        )
        .setJmxEnabled(true)
        .setMetricLogReporterMillis(TimeUnit.SECONDS.toMillis(5))
        .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        FlexyPoolConfiguration<DataSource> configuration = configuration();
        return new FlexyPoolDataSource<DataSource>(configuration,
                new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory( 5),
                new RetryConnectionAcquisitionStrategy.Factory( 2)
        );
    }
}

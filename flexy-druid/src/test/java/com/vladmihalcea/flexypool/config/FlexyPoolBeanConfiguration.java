package com.vladmihalcea.flexypool.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.DruidAdapter;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquisitionStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquisitionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * FlexyPoolConfiguration - Configuration for flexypool data source
 *
 * @author Weapon Lin
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolBeanConfiguration {

	@Autowired
	private DruidDataSource poolingDataSource;

	@Value("${flexy.pool.uniqueId}")
	private String uniqueId;

	@Bean
	public FlexyPoolConfiguration<DruidDataSource> configuration() {
		return new FlexyPoolConfiguration.Builder<DruidDataSource>(
				uniqueId,
				poolingDataSource,
				DruidAdapter.FACTORY
		).build();
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public FlexyPoolDataSource dataSource() {
		FlexyPoolConfiguration<DruidDataSource> configuration = configuration();
		return new FlexyPoolDataSource<DruidDataSource>(
				configuration,
				new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory( 5 ),
				new RetryConnectionAcquisitionStrategy.Factory( 2 )
		);
	}
}

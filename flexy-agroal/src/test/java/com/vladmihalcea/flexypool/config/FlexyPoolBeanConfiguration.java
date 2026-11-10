package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.AgroalPoolAdapter;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquisitionStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquisitionStrategy;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;
import java.time.Duration;

/**
 * FlexyPoolBeanConfiguration - Configuration for the Agroal FlexyPool data source
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolBeanConfiguration {

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    /**
     * Agroal has no bean-style setters, so the pool is built from its configuration supplier.
     */
    @Bean(destroyMethod = "close")
    public AgroalDataSource poolingDataSource() throws SQLException {
        return AgroalDataSource.from(
            new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration(pool -> pool
                    .minSize(1)
                    .maxSize(3)
                    .acquisitionTimeout(Duration.ofSeconds(1))
                    .connectionFactoryConfiguration(factory -> factory
                        .jdbcUrl(jdbcUrl)
                        .principal(new NamePrincipal(username))
                        .credential(new SimplePassword(password))
                    )
                )
        );
    }

    @Bean
    public FlexyPoolConfiguration<AgroalDataSource> configuration() throws SQLException {
        return new FlexyPoolConfiguration.Builder<AgroalDataSource>(
            uniqueId,
            poolingDataSource(),
            AgroalPoolAdapter.FACTORY
        ).build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() throws SQLException {
        FlexyPoolConfiguration<AgroalDataSource> configuration = configuration();
        return new FlexyPoolDataSource<AgroalDataSource>(
            configuration,
            new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory(5),
            new RetryConnectionAcquisitionStrategy.Factory(2)
        );
    }
}

package com.vladmihalcea.flexypool.config;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.BitronixPoolAdapter;
import com.vladmihalcea.flexypool.connection.ConnectionDecoratorFactoryResolver;
import com.vladmihalcea.flexypool.event.*;
import com.vladmihalcea.flexypool.metric.MetricsFactoryResolver;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * FlexyDataSourceConfiguration - Configuration for flexypool data source
 *
 * @author Vlad Mihalcea
 */
@org.springframework.context.annotation.Configuration
public class FlexyPoolConfiguration {

    public static class ConnectionAcquireTimeThresholdExceededEventListener
            extends EventListener<ConnectionAcquireTimeThresholdExceededEvent> {

        public static final Logger LOGGER = LoggerFactory.getLogger(
                ConnectionAcquireTimeThresholdExceededEventListener.class);

        public ConnectionAcquireTimeThresholdExceededEventListener() {
            super(ConnectionAcquireTimeThresholdExceededEvent.class);
        }

        @Override
        public void on(ConnectionAcquireTimeThresholdExceededEvent event) {
            LOGGER.info("Caught event {}", event);
        }
    }

    public static class ConnectionLeaseTimeThresholdExceededEventListener
            extends EventListener<ConnectionLeaseTimeThresholdExceededEvent> {

        public static final Logger LOGGER = LoggerFactory.getLogger(
                ConnectionLeaseTimeThresholdExceededEventListener.class);

        public ConnectionLeaseTimeThresholdExceededEventListener() {
            super(ConnectionLeaseTimeThresholdExceededEvent.class);
        }

        @Override
        public void on(ConnectionLeaseTimeThresholdExceededEvent event) {
            LOGGER.info("Caught event {}", event);
        }
    }

    public static class ConnectionAcquireTimeoutEventListener
            extends EventListener<ConnectionAcquireTimeoutEvent> {

        public static final Logger LOGGER = LoggerFactory.getLogger(
                ConnectionAcquireTimeoutEventListener.class);

        public ConnectionAcquireTimeoutEventListener() {
            super(ConnectionAcquireTimeoutEvent.class);
        }

        @Override
        public void on(ConnectionAcquireTimeoutEvent event) {
            LOGGER.info("Caught event {}", event);
        }
    }

    @Autowired
    private PoolingDataSource poolingDataSource;

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;



    @Bean
    public Configuration<PoolingDataSource> configuration() {
        return new Configuration.Builder<PoolingDataSource>(
                uniqueId,
                poolingDataSource,
                BitronixPoolAdapter.FACTORY
        )
        .setMetricsFactory(MetricsFactoryResolver.INSTANCE.resolve())
        .setConnectionProxyFactory(ConnectionDecoratorFactoryResolver.INSTANCE.resolve())
        .setMetricLogReporterMillis(TimeUnit.SECONDS.toMillis(5))
        .setJmxEnabled(true)
        .setJmxAutoStart(true)
        .setConnectionAcquireTimeThresholdMillis(50L)
        .setConnectionLeaseTimeThresholdMillis(250L)
        .setEventListenerResolver(new EventListenerResolver() {
            @Override
            public List<EventListener<? extends Event>> resolveListeners() {
                return Arrays.<EventListener<? extends Event>>asList(
                    new ConnectionAcquireTimeoutEventListener(),
                    new ConnectionAcquireTimeThresholdExceededEventListener(),
                    new ConnectionLeaseTimeThresholdExceededEventListener()
                );
            }
        })
        .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        Configuration<PoolingDataSource> configuration = configuration();
        return new FlexyPoolDataSource<PoolingDataSource>(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory(5),
                new RetryConnectionAcquiringStrategy.Factory(2)
        );
    }
}

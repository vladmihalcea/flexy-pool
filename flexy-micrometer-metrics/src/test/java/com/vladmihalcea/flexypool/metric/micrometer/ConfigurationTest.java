package com.vladmihalcea.flexypool.metric.micrometer;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.config.FlexyPoolConfiguration;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;

import org.junit.Test;
import org.mockito.Mockito;
import io.micrometer.core.instrument.MeterRegistry;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ConfigurationTest - Configuration Test
 *
 * @author Vlad Mihalcea
 */
public class ConfigurationTest {

    @Test
    public void testBuilder() {
        final MeterRegistry meterRegistry = Mockito.mock(MeterRegistry.class);

        DataSource dataSource = Mockito.mock(DataSource.class);
        PoolAdapterFactory<DataSource> poolAdapterFactory = Mockito.mock(PoolAdapterFactory.class);
        ConnectionProxyFactory connectionProxyFactory = Mockito.mock(ConnectionProxyFactory.class);
        Metrics metrics = Mockito.mock(Metrics.class);
        PoolAdapter poolAdapter = Mockito.mock(PoolAdapter.class);
        when(poolAdapterFactory.newInstance(any(ConfigurationProperties.class))).thenReturn(poolAdapter);
        FlexyPoolConfiguration<DataSource> configuration = new FlexyPoolConfiguration.Builder<DataSource>(
                "unique", dataSource, poolAdapterFactory)
                .setConnectionProxyFactory(connectionProxyFactory)
                .setJmxAutoStart(true)
                .setJmxEnabled(true)
                .setMetricLogReporterMillis(120)
                .setMetricsFactory(new MetricsFactory() {
                    @Override public Metrics newInstance(ConfigurationProperties configurationProperties) {
                        return new MicrometerMetrics(configurationProperties, meterRegistry);
                    }
                })
                .build();
        assertSame("unique", configuration.getUniqueName());
        assertSame(connectionProxyFactory, configuration.getConnectionProxyFactory());
        assertTrue(configuration.isJmxAutoStart());
        assertTrue(configuration.isJmxEnabled());
        assertEquals(120, configuration.getMetricLogReporterMillis());
        assertSame(poolAdapter, configuration.getPoolAdapter());
        assertSame(dataSource, configuration.getTargetDataSource());
    }
}

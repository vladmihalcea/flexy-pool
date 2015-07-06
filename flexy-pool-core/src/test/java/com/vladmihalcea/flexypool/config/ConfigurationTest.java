package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * ConfigurationTest - Configuration Test
 *
 * @author Vlad Mihalcea
 */
public class ConfigurationTest {

    @Test
    public void testBuilder() {
        DataSource dataSource = Mockito.mock(DataSource.class);
        PoolAdapterFactory<DataSource> poolAdapterFactory = Mockito.mock(PoolAdapterFactory.class);
        ConnectionProxyFactory connectionProxyFactory = Mockito.mock(ConnectionProxyFactory.class);
        MetricsFactory metricsFactory = Mockito.mock(MetricsFactory.class);
        Metrics metrics = Mockito.mock(Metrics.class);
        PoolAdapter poolAdapter = Mockito.mock(PoolAdapter.class);
        when(metricsFactory.newInstance(any(ConfigurationProperties.class))).thenReturn(metrics);
        when(poolAdapterFactory.newInstance(any(ConfigurationProperties.class))).thenReturn(poolAdapter);
        Configuration<DataSource> configuration = new Configuration.Builder<DataSource>(
            "unique", dataSource, poolAdapterFactory)
        .setConnectionProxyFactory(connectionProxyFactory)
        .setJmxAutoStart(true)
        .setJmxEnabled(true)
        .setMetricLogReporterMillis(120)
        .setMetricsFactory(metricsFactory)
        .build();
        assertSame("unique", configuration.getUniqueName());
        assertSame(connectionProxyFactory, configuration.getConnectionProxyFactory());
        assertTrue(configuration.isJmxAutoStart());
        assertTrue(configuration.isJmxEnabled());
        assertEquals(120, configuration.getMetricLogReporterMillis());
        assertSame(metrics, configuration.getMetrics());
        assertSame(poolAdapter, configuration.getPoolAdapter());
        assertSame(dataSource, configuration.getTargetDataSource());
    }
}
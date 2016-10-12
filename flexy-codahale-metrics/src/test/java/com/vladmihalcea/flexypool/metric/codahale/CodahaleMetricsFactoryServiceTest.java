package com.vladmihalcea.flexypool.metric.codahale;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * CodahaleMetricsFactoryServiceTest - CodahaleMetricsFactoryService Test
 *
 * @author Vlad Mihalcea
 */
public class CodahaleMetricsFactoryServiceTest {

    @Test
    public void testLoadSuccess() {
        MetricsFactory metricsFactory = new CodahaleMetricsFactoryService().load();
        assertNotNull(metricsFactory);
        assertSame(CodahaleMetrics.FACTORY, metricsFactory);
    }

    @Test
    public void testLoadFailure() {
        ClassLoader currentClassLoader = ClassLoaderUtils.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(new ClassLoader() {
                @Override
                protected Class loadClass(String class_name, boolean resolve) throws ClassNotFoundException {
                    if(class_name.equals("com.codahale.metrics.Metric")) {
                        return null;
                    }
                    return super.loadClass(class_name, resolve);
                }
            });
            MetricsFactory metricsFactory = new CodahaleMetricsFactoryService().load();
            assertNull(metricsFactory);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    @Test
    public void testExistingCodahaleMetricsFactory() {
        DataSource dataSource = Mockito.mock(DataSource.class);
        PoolAdapterFactory<DataSource> poolAdapterFactory = Mockito.mock(PoolAdapterFactory.class);

        PoolAdapter poolAdapter = Mockito.mock(PoolAdapter.class);
        when(poolAdapterFactory.newInstance(any(ConfigurationProperties.class))).thenReturn(poolAdapter);

        ReservoirFactory reservoirFactory = Mockito.mock(ReservoirFactory.class);

        Configuration<DataSource> configuration = new Configuration.Builder<DataSource>(
                "unique", dataSource, poolAdapterFactory)
                .setMetricsFactory(new CodahaleMetrics.ReservoirMetricsFactory(reservoirFactory))
                .build();

        assertEquals(CodahaleHistogram.class, configuration.getMetrics().histogram("test").getClass());
    }
}
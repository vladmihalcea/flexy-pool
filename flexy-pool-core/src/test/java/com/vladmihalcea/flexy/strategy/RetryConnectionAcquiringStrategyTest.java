package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.adaptor.PoolAdapterBuilder;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.metric.Histogram;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.metric.MetricsBuilder;
import com.vladmihalcea.flexy.util.ConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * RetryConnectionAcquiringStrategyTest - RetryConnectionAcquiringStrategy Test
 *
 * @author Vlad Mihalcea
 */
public class RetryConnectionAcquiringStrategyTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private PoolAdapter<DataSource> poolAdapter;

    @Mock
    private Connection connection;

    @Mock
    private Metrics metrics;

    @Mock
    private Histogram histogram;

    private Configuration<DataSource> configuration;

    private ConnectionRequestContext connectionRequestContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        configuration = new Configuration.Builder<DataSource>(
                getClass().getName(),
                dataSource,
                new MetricsBuilder() {
                    @Override
                    public Metrics build(ConfigurationProperties configurationProperties) {
                        return metrics;
                    }
                },
                new PoolAdapterBuilder<DataSource>() {
                    @Override
                    public PoolAdapter<DataSource> build(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
                        return poolAdapter;
                    }
                }
        )
                .build();
        when(metrics.histogram(RetryConnectionAcquiringStrategy.RETRY_ATTEMPTS_HISTOGRAM)).thenReturn(histogram);
        connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(poolAdapter.getTargetDataSource()).thenReturn(dataSource);
    }

    @Test
    public void testInvalidRetryAttempts() {
        try {
            new RetryConnectionAcquiringStrategy.Builder<DataSource>(0).build(configuration);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("retryAttempts must ge greater than 0!", e.getMessage());
        }
    }

    @Test
    public void testConnectionAcquiredInOneAttempt() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenReturn(connection);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy.Builder<DataSource>(5).build(configuration);
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        assertSame(connection, retryConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        verify(histogram, never()).update(anyInt());
    }

    @Test
    public void testConnectionAcquiredInTwoAttempts() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new AcquireTimeoutException(new Exception()))
                .thenReturn(connection);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy.Builder<DataSource>(5).build(configuration);
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        assertSame(connection, retryConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        assertEquals(1, connectionRequestContext.getRetryAttempts());
        verify(histogram, times(1)).update(1);
    }

    @Test
    public void testConnectionNotAcquiredAfterAllAttempts() throws SQLException {
        Exception rootException = new Exception();
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new AcquireTimeoutException(rootException));
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy.Builder<DataSource>(2).build(configuration);
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        try {
            retryConnectionAcquiringStrategy.getConnection(connectionRequestContext);
        } catch (AcquireTimeoutException e) {
            assertSame(rootException, e.getCause());
        }
        assertEquals(2, connectionRequestContext.getRetryAttempts());
        verify(histogram, times(1)).update(2);
    }
}

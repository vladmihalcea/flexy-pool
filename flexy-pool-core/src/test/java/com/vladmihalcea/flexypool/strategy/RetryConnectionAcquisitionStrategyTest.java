package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.config.FlexyPoolConfiguration;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.exception.ConnectionAcquisitionTimeoutException;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * RetryConnectionAcquisitionStrategyTest - RetryConnectionAcquisitionStrategy Test
 *
 * @author Vlad Mihalcea
 */
public class RetryConnectionAcquisitionStrategyTest {

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

    private FlexyPoolConfiguration<DataSource> configuration;

    private ConnectionRequestContext connectionRequestContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        configuration = new FlexyPoolConfiguration.Builder<DataSource>(
                getClass().getName(),
                dataSource,
                new PoolAdapterFactory<DataSource>() {
                    @Override
                    public PoolAdapter<DataSource> newInstance(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
                        return poolAdapter;
                    }
                }
        )
                .setMetricsFactory(new MetricsFactory() {
                    @Override
                    public Metrics newInstance(ConfigurationProperties configurationProperties) {
                        return metrics;
                    }
                })
                .build();
        when(metrics.histogram( RetryConnectionAcquisitionStrategy.RETRY_ATTEMPTS_HISTOGRAM)).thenReturn( histogram);
        connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(poolAdapter.getTargetDataSource()).thenReturn(dataSource);
    }

    @Test
    public void testInvalidRetryAttempts() {
        try {
            new RetryConnectionAcquisitionStrategy.Factory<DataSource>( 0).newInstance( configuration);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("retryAttempts must ge greater than 0!", e.getMessage());
        }
    }

    @Test
    public void testConnectionAcquiredInOneAttempt() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenReturn(connection);
        RetryConnectionAcquisitionStrategy RetryConnectionAcquisitionStrategy = new RetryConnectionAcquisitionStrategy.Factory<DataSource>( 5).newInstance( configuration);
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        assertSame(connection, RetryConnectionAcquisitionStrategy.getConnection(connectionRequestContext));
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        verify(histogram, never()).update(anyInt());
    }

    @Test
    public void testConnectionAcquiredInTwoAttempts() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new ConnectionAcquisitionTimeoutException( new Exception()))
                .thenReturn(connection);
        RetryConnectionAcquisitionStrategy RetryConnectionAcquisitionStrategy = new RetryConnectionAcquisitionStrategy.Factory<DataSource>( 5).newInstance( configuration);
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        assertSame(connection, RetryConnectionAcquisitionStrategy.getConnection(connectionRequestContext));
        assertEquals(1, connectionRequestContext.getRetryAttempts());
        verify(histogram, times(1)).update(1);
    }

    @Test
    public void testConnectionNotAcquiredAfterAllAttempts() throws SQLException {
        Exception rootException = new Exception();
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new ConnectionAcquisitionTimeoutException( rootException));
        RetryConnectionAcquisitionStrategy RetryConnectionAcquisitionStrategy = new RetryConnectionAcquisitionStrategy.Factory<DataSource>( 2).newInstance( configuration);
        assertEquals(0, connectionRequestContext.getRetryAttempts());
        try {
            RetryConnectionAcquisitionStrategy.getConnection(connectionRequestContext);
        } catch (ConnectionAcquisitionTimeoutException e) {
            assertSame(rootException, e.getCause());
        }
        assertEquals(2, connectionRequestContext.getRetryAttempts());
        verify(histogram, times(1)).update(2);
    }
}

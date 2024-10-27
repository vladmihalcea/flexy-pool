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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * IncrementPoolOnTimeoutConnectionAcquiringStrategyTest - IncrementPoolOnTimeoutConnectionAcquiringStrategy Test
 *
 * @author Vlad Mihalcea
 */
public class IncrementPoolOnTimeoutConnectionAcquisitionStrategyTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private PoolAdapter<DataSource> poolAdapter;

    @Mock
    private Connection connection;

    @Mock
    private Metrics metrics;

    @Mock
    private Histogram maxPoolSizeHistogram;

    @Mock
    private Histogram overgrowPoolSizeHistogram;

    private FlexyPoolConfiguration<DataSource> configuration;

    private ConnectionRequestContext connectionRequestContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        configuration = new FlexyPoolConfiguration.Builder<>(
				getClass().getName(),
				dataSource,
				configurationProperties -> poolAdapter)
        .setMetricsFactory(configurationProperties -> metrics)
        .build();
        when(metrics.histogram(IncrementPoolOnTimeoutConnectionAcquisitionStrategy.MAX_POOL_SIZE_HISTOGRAM)).thenReturn( maxPoolSizeHistogram);
        when(metrics.histogram(IncrementPoolOnTimeoutConnectionAcquisitionStrategy.OVERGROW_POOL_SIZE_HISTOGRAM)).thenReturn(
                overgrowPoolSizeHistogram );
        connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(poolAdapter.getTargetDataSource()).thenReturn(dataSource);
    }

    @Test
    public void testConnectionAcquiredInOneAttempt() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenReturn(connection);
        when(poolAdapter.getMaxPoolSize()).thenReturn(1);
        IncrementPoolOnTimeoutConnectionAcquisitionStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory<DataSource>( 5).newInstance( configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, never()).setMaxPoolSize(anyInt());
        verify(maxPoolSizeHistogram, times(1)).update(1);
        verify( overgrowPoolSizeHistogram, never()).update( anyLong());
    }

    @Test
    public void testConnectionAcquiredInTwoAttempts() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new ConnectionAcquisitionTimeoutException( new Exception()))
                .thenReturn(connection);
        when(poolAdapter.getMaxPoolSize()).thenReturn(2);
        IncrementPoolOnTimeoutConnectionAcquisitionStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory<DataSource>( 5).newInstance( configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, times(1)).setMaxPoolSize(3);
        verify(maxPoolSizeHistogram, times(1)).update(2);
        verify(maxPoolSizeHistogram, times(1)).update(3);
        verify( overgrowPoolSizeHistogram, times( 1)).update( 1);
    }

    @Test
    public void testConnectionNotAcquiredAfterAllAttempts() throws SQLException {
        Exception rootException = new Exception();
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new ConnectionAcquisitionTimeoutException( rootException));
        final AtomicInteger maxPoolSize = new AtomicInteger(2);
        when(poolAdapter.getMaxPoolSize()).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return maxPoolSize.get();
            }
        });
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Integer nextPoolSize = (Integer) invocationOnMock.getArguments()[0];
                maxPoolSize.set(nextPoolSize);
                return nextPoolSize;
            }
        }).when(poolAdapter).setMaxPoolSize(anyInt());
        IncrementPoolOnTimeoutConnectionAcquisitionStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory<DataSource>( 5).newInstance( configuration);
        try {
            incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext);
        } catch (SQLException e) {
            assertSame(rootException, e.getCause());
        }
        verify(poolAdapter, times(1)).setMaxPoolSize(3);
        verify(poolAdapter, times(1)).setMaxPoolSize(4);
        verify(poolAdapter, times(1)).setMaxPoolSize(5);
        verify(maxPoolSizeHistogram, times(1)).update(2);
        verify(maxPoolSizeHistogram, times(1)).update(3);
        verify(maxPoolSizeHistogram, times(1)).update(4);
        verify(maxPoolSizeHistogram, times(1)).update(5);
        verify( overgrowPoolSizeHistogram, times( 1)).update( 1);
        verify( overgrowPoolSizeHistogram, times( 1)).update( 2);
        verify( overgrowPoolSizeHistogram, times( 1)).update( 3);
    }

    @Test
    public void testConnectionAcquiredInOneAttemptWithTimeoutThreshold() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(150);
                return connection;
            }
        });
        when(poolAdapter.getMaxPoolSize()).thenReturn(1);
        IncrementPoolOnTimeoutConnectionAcquisitionStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory<DataSource>( 5, 100).newInstance( configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, times(1)).setMaxPoolSize(2);
        verify(maxPoolSizeHistogram, times(1)).update(1);
        verify(maxPoolSizeHistogram, times(1)).update(2);
        verify( overgrowPoolSizeHistogram, times( 1)).update( 1);
    }

    @Test
    public void testConnectionAcquiredInOneAttemptWithTimeoutThresholdMaxSizeReached() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(150);
                return connection;
            }
        });
        when(poolAdapter.getMaxPoolSize()).thenReturn(5);
        IncrementPoolOnTimeoutConnectionAcquisitionStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory<DataSource>( 5, 100).newInstance( configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, never()).setMaxPoolSize(anyInt());
        verify(maxPoolSizeHistogram, times(1)).update(5);
        verify( overgrowPoolSizeHistogram, never()).update( anyLong());
    }

    @Test
    public void testConnectionAcquiredInOneAttemptWithTimeoutThresholdMaxSizeReachedConcurrently() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(150);
                return connection;
            }
        });
        when(poolAdapter.getMaxPoolSize())
                .thenReturn(3, 4, 4, 5);
        IncrementPoolOnTimeoutConnectionAcquisitionStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquisitionStrategy.Factory<DataSource>( 5, 100).newInstance( configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, never()).setMaxPoolSize(anyInt());
        verify(maxPoolSizeHistogram, times(1)).update(3);
        verify( overgrowPoolSizeHistogram, never()).update( anyLong());
    }
}

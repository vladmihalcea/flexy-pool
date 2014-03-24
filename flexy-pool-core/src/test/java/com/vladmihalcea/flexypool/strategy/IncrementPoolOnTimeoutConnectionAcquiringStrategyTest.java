package com.vladmihalcea.flexypool.strategy;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterBuilder;
import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsBuilder;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * IncrementPoolOnTimeoutConnectionAcquiringStrategyTest - IncrementPoolOnTimeoutConnectionAcquiringStrategy Test
 *
 * @author Vlad Mihalcea
 */
public class IncrementPoolOnTimeoutConnectionAcquiringStrategyTest {

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
        when(metrics.histogram(IncrementPoolOnTimeoutConnectionAcquiringStrategy.MAX_POOL_SIZE_HISTOGRAM)).thenReturn(maxPoolSizeHistogram);
        connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(poolAdapter.getTargetDataSource()).thenReturn(dataSource);
    }

    @Test
    public void testConnectionAcquiredInOneAttempt() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext))).thenReturn(connection);
        when(poolAdapter.getMaxPoolSize()).thenReturn(1);
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder<DataSource>(5).build(configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, never()).setMaxPoolSize(anyInt());
        verify(maxPoolSizeHistogram, times(1)).update(1);
    }

    @Test
    public void testConnectionAcquiredInTwoAttempts() throws SQLException {
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new AcquireTimeoutException(new Exception()))
                .thenReturn(connection);
        when(poolAdapter.getMaxPoolSize()).thenReturn(2);
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder<DataSource>(5).build(configuration);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(connectionRequestContext));
        verify(poolAdapter, times(1)).setMaxPoolSize(3);
        verify(maxPoolSizeHistogram, times(1)).update(2);
        verify(maxPoolSizeHistogram, times(1)).update(3);
    }

    @Test
    public void testConnectionNotAcquiredAfterAllAttempts() throws SQLException {
        Exception rootException = new Exception();
        when(poolAdapter.getConnection(same(connectionRequestContext)))
                .thenThrow(new AcquireTimeoutException(rootException));
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
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Builder<DataSource>(5).build(configuration);
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
    }
}

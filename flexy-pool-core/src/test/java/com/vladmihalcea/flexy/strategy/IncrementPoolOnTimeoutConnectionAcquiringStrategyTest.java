package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.config.FlexyConfiguration;
import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
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
    private PoolAdapter poolAdapter;

    @Mock
    private Connection connection;

    private FlexyConfiguration configuration;

    @Before
    public void before() {
        configuration = new FlexyConfiguration(UUID.randomUUID().toString());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConnectionAcquiredInOneAttempt() throws SQLException {
        ConnectionRequestContext context = new ConnectionRequestContext.Builder(configuration).build();
        when(poolAdapter.getConnection(same(context))).thenReturn(connection);
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquiringStrategy(poolAdapter, 5);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(context));
        verify(poolAdapter, never()).setMaxPoolSize(anyInt());
        assertEquals(0, context.getOverflowPoolSize());
    }

    @Test
    public void testConnectionAcquiredInTwoAttempts() throws SQLException {
        ConnectionRequestContext context = new ConnectionRequestContext.Builder(configuration).build();
        when(poolAdapter.getConnection(same(context)))
                .thenThrow(new AcquireTimeoutException(new Exception()))
                .thenReturn(connection);
        when(poolAdapter.getMaxPoolSize()).thenReturn(2);
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquiringStrategy(poolAdapter, 5);
        assertSame(connection, incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(context));
        verify(poolAdapter, times(1)).setMaxPoolSize(3);
        assertEquals(1, context.getOverflowPoolSize());
    }

    @Test
    public void testConnectionNotAcquiredAfterAllAttempts() throws SQLException {
        ConnectionRequestContext context = new ConnectionRequestContext.Builder(configuration).build();
        Exception rootException = new Exception();
        when(poolAdapter.getConnection(same(context)))
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
        IncrementPoolOnTimeoutConnectionAcquiringStrategy incrementPoolOnTimeoutConnectionAcquiringStrategy = new IncrementPoolOnTimeoutConnectionAcquiringStrategy(poolAdapter, 5);
        try {
            incrementPoolOnTimeoutConnectionAcquiringStrategy.getConnection(context);
        } catch (SQLException e) {
            assertSame(rootException, e.getCause());
        }
        verify(poolAdapter, times(1)).setMaxPoolSize(3);
        verify(poolAdapter, times(1)).setMaxPoolSize(4);
        verify(poolAdapter, times(1)).setMaxPoolSize(5);
        assertEquals(3, context.getOverflowPoolSize());
    }
}

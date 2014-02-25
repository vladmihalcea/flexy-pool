package com.vladmihalcea.flexy.strategy;

import com.vladmihalcea.flexy.ConnectionRequestContext;
import com.vladmihalcea.flexy.PoolAdapter;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

/**
 * RetryConnectionAcquiringStrategyTest - RetryConnectionAcquiringStrategy Test
 *
 * @author Vlad Mihalcea
 */
public class RetryConnectionAcquiringStrategyTest {

    @Mock
    private PoolAdapter poolAdapter;

    @Mock
    private Connection connection;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConnectionAcquiredInOneAttempt() throws SQLException {
        ConnectionRequestContext context = new ConnectionRequestContext.Builder().build();
        when(poolAdapter.getConnection(same(context))).thenReturn(connection);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy(poolAdapter, 5);
        assertEquals(0, context.getAttempts());
        assertSame(connection, retryConnectionAcquiringStrategy.getConnection(context));
        assertEquals(1, context.getAttempts());
    }

    @Test
    public void testConnectionAcquiredInTwoAttempts() throws SQLException {
        ConnectionRequestContext context = new ConnectionRequestContext.Builder().build();
        when(poolAdapter.getConnection(same(context)))
                .thenThrow(new AcquireTimeoutException(new Exception()))
                .thenReturn(connection);
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy(poolAdapter, 5);
        assertEquals(0, context.getAttempts());
        assertSame(connection, retryConnectionAcquiringStrategy.getConnection(context));
        assertEquals(2, context.getAttempts());
    }

    @Test
    public void testConnectionNotAcquiredAfterAllAttempts() throws SQLException {
        ConnectionRequestContext context = new ConnectionRequestContext.Builder().build();
        Exception rootException = new Exception();
        when(poolAdapter.getConnection(same(context)))
                .thenThrow(new AcquireTimeoutException(rootException));
        RetryConnectionAcquiringStrategy retryConnectionAcquiringStrategy = new RetryConnectionAcquiringStrategy(poolAdapter, 2);
        assertEquals(0, context.getAttempts());
        try {
            retryConnectionAcquiringStrategy.getConnection(context);
        } catch (AcquireTimeoutException e) {
            assertSame(rootException, e.getCause());
        }
        assertEquals(3, context.getAttempts());
    }
}

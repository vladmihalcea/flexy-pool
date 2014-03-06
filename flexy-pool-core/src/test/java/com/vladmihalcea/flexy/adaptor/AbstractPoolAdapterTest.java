package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.context.Context;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.metric.Timer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * AbstractPoolAdapterTest - AbstractPoolAdapter Test
 *
 * @author Vlad Mihalcea
 */
public class AbstractPoolAdapterTest {

    public static class TestPoolAdaptor extends AbstractPoolAdapter<DataSource> {

        public TestPoolAdaptor(Context context, DataSource dataSource) {
            super(context, dataSource);
        }

        @Override
        public int getMaxPoolSize() {
            return 10;
        }

        @Override
        public void setMaxPoolSize(int maxPoolSize) {

        }

        @Override
        public int getMinPoolSize() {
            return 5;
        }

        @Override
        public void setMinPoolSize(int minPoolSize) {

        }
    }

    @Mock
    private DataSource dataSource;

    @Mock
    private PoolAdapter poolAdapter;

    @Mock
    private Connection connection;

    @Mock
    private Metrics metrics;

    @Mock
    private Timer timer;

    private AbstractPoolAdapter<DataSource> abstractPoolAdapter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Configuration configuration = new Configuration(UUID.randomUUID().toString());
        Context context = new Context(configuration, metrics);
        when(metrics.timer(AbstractPoolAdapter.CONNECTION_ACQUIRE_MILLIS)).thenReturn(timer);
        abstractPoolAdapter = new TestPoolAdaptor(context, dataSource);
    }

    @Test
    public void testGetConnectionWithoutCredentials() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(dataSource.getConnection()).thenReturn(connection);
        assertSame(connection, abstractPoolAdapter.getConnection(connectionRequestContext));
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWithCredentials() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder()
                .setCredentials(new Credentials("username", "password")).build();
        when(dataSource.getConnection(eq("username"), eq("password"))).thenReturn(connection);
        assertSame(connection, abstractPoolAdapter.getConnection(connectionRequestContext));
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWithoutCredentialsThrowsSQLException() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(dataSource.getConnection()).thenThrow(new SQLException());
        try {
            abstractPoolAdapter.getConnection(connectionRequestContext);
            fail("Should have thrown SQLException");
        } catch (SQLException e) {
            verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        }
    }

    @Test
    public void testGetConnectionWithoutCredentialsThrowsRuntimeException() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(dataSource.getConnection()).thenThrow(new RuntimeException());
        try {
            abstractPoolAdapter.getConnection(connectionRequestContext);
            fail("Should have thrown SQLException");
        } catch (RuntimeException e) {
            verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        }
    }
}

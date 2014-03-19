package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.builder.MetricsBuilder;
import com.vladmihalcea.flexy.builder.PoolAdapterBuilder;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.metric.Timer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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

        public TestPoolAdaptor(Configuration<DataSource> configuration) {
            super(configuration);
        }

        @Override
        public int getMaxPoolSize() {
            return 10;
        }

        @Override
        public void setMaxPoolSize(int maxPoolSize) {

        }
    }

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Metrics metrics;

    @Mock
    private Timer timer;

    private AbstractPoolAdapter<DataSource> poolAdapter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(metrics.timer(AbstractPoolAdapter.CONNECTION_ACQUIRE_MILLIS)).thenReturn(timer);
        Configuration<DataSource> configuration = configuration = new Configuration.Builder<DataSource>(
                getClass().getName(),
                dataSource,
                new MetricsBuilder() {
                    @Override
                    public Metrics build(Configuration configuration) {
                        return metrics;
                    }
                },
                new PoolAdapterBuilder<DataSource>() {
                    @Override
                    public PoolAdapter<DataSource> build(Configuration<DataSource> configuration) {
                        return poolAdapter;
                    }
                }
        )
        .build();
        poolAdapter = new TestPoolAdaptor(configuration);
    }

    @Test
    public void testGetConnectionWithoutCredentials() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(dataSource.getConnection()).thenReturn(connection);
        assertSame(connection, poolAdapter.getConnection(connectionRequestContext));
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWithCredentials() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder()
                .setCredentials(new Credentials("username", "password")).build();
        when(dataSource.getConnection(eq("username"), eq("password"))).thenReturn(connection);
        assertSame(connection, poolAdapter.getConnection(connectionRequestContext));
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWithoutCredentialsThrowsSQLException() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(dataSource.getConnection()).thenThrow(new SQLException());
        try {
            poolAdapter.getConnection(connectionRequestContext);
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
            poolAdapter.getConnection(connectionRequestContext);
            fail("Should have thrown SQLException");
        } catch (SQLException e) {
            verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        }
    }
}

package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.config.FlexyPoolConfiguration;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.event.ConnectionAcquisitionTimeoutEvent;
import com.vladmihalcea.flexypool.event.Event;
import com.vladmihalcea.flexypool.event.EventListener;
import com.vladmihalcea.flexypool.event.EventListenerResolver;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.Timer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * AbstractPoolAdapterTest - AbstractPoolAdapter Test
 *
 * @author Vlad Mihalcea
 */
public class AbstractPoolAdapterTest {

    public static class ConnectionTimedOutException extends SQLException {

    }

    public static class ConnectionTimedOutExceptionEventListener extends EventListener<ConnectionAcquisitionTimeoutEvent> {

        private ConnectionAcquisitionTimeoutEvent event;

        public ConnectionTimedOutExceptionEventListener() {
            super( ConnectionAcquisitionTimeoutEvent.class);
        }

        @Override
        public void on(ConnectionAcquisitionTimeoutEvent event) {
            this.event = event;
        }
    }

    public static class TestPoolAdapter extends AbstractPoolAdapter<DataSource> {

        public TestPoolAdapter(ConfigurationProperties<DataSource, Metrics, PoolAdapter<DataSource>> configurationProperties) {
            super(configurationProperties);
        }

        @Override
        public int getMaxPoolSize() {
            return 10;
        }

        @Override
        public void setMaxPoolSize(int maxPoolSize) {

        }

        @Override
        protected boolean isTimeoutAcquisitionException(Exception e) {
            return e instanceof ConnectionTimedOutException;
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

    private ConnectionTimedOutExceptionEventListener eventListener = new ConnectionTimedOutExceptionEventListener();

    private AbstractPoolAdapter<DataSource> poolAdapter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(metrics.timer(AbstractPoolAdapter.CONNECTION_ACQUISITION_MILLIS )).thenReturn( timer);

        FlexyPoolConfiguration<DataSource> configuration = new FlexyPoolConfiguration.Builder<DataSource>(
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
        .setEventListenerResolver(new EventListenerResolver() {
            @Override
            public List<EventListener<? extends Event>> resolveListeners() {
                return Collections.<EventListener<? extends Event>>singletonList(eventListener);
            }
        })
        .build();
        poolAdapter = newPoolAdapter(configuration);
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

    @Test
    public void testGetConnectionThrowsTimeoutException() throws SQLException {
        ConnectionRequestContext connectionRequestContext = new ConnectionRequestContext.Builder().build();
        when(dataSource.getConnection()).thenThrow(new ConnectionTimedOutException());
        try {
            poolAdapter.getConnection(connectionRequestContext);
            fail("Should have thrown SQLException");
        } catch (SQLException e) {
            verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
            if (supportsTimeoutExceptionTranslation()) {
                ConnectionAcquisitionTimeoutEvent connectionAcquisitionTimeoutEvent = eventListener.event;
                assertNotNull(connectionAcquisitionTimeoutEvent);
                assertEquals(getClass().getName(), connectionAcquisitionTimeoutEvent.getUniqueName());
            }
        }
    }

    protected AbstractPoolAdapter<DataSource> newPoolAdapter(FlexyPoolConfiguration<DataSource> configuration) {
        return new TestPoolAdapter(configuration);
    }

    protected AbstractPoolAdapter<DataSource> getPoolAdapter() {
        return poolAdapter;
    }

    protected boolean supportsTimeoutExceptionTranslation() {
        return true;
    }
}

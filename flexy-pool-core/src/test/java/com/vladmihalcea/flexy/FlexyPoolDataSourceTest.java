package com.vladmihalcea.flexy;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.connection.Credentials;
import com.vladmihalcea.flexy.exception.AcquireTimeoutException;
import com.vladmihalcea.flexy.exception.CantAcquireConnectionException;
import com.vladmihalcea.flexy.factory.MetricsFactory;
import com.vladmihalcea.flexy.factory.PoolAdapterFactory;
import com.vladmihalcea.flexy.metric.Metrics;
import com.vladmihalcea.flexy.metric.Timer;
import com.vladmihalcea.flexy.strategy.ConnectionAcquiringStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * FlexyPoolDataSourceTest - FlexyPoolDataSource Test
 *
 * @author Vlad Mihalcea
 */
public class FlexyPoolDataSourceTest {

    @Mock
    private ConnectionAcquiringStrategy connectionAcquiringStrategy;

    @Mock
    private PoolAdapter<DataSource> poolAdapter;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Metrics metrics;

    @Mock
    private Timer timer;

    private Configuration<DataSource> configuration;

    private FlexyPoolDataSource flexyPoolDataSource;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        configuration = new Configuration.Builder<DataSource>(
                getClass().getName(),
                dataSource,
                new MetricsFactory() {
                    @Override
                    public Metrics newInstance(Configuration configuration) {
                        return metrics;
                    }
                },
                new PoolAdapterFactory<DataSource>() {
                    @Override
                    public PoolAdapter<DataSource> newInstance(Configuration<DataSource> configuration) {
                        return poolAdapter;
                    }
                }
        )
                .build();
        when(metrics.timer(FlexyPoolDataSource.OVERALL_CONNECTION_ACQUIRE_MILLIS)).thenReturn(timer);
        when(poolAdapter.getTargetDataSource()).thenReturn(dataSource);
        this.flexyPoolDataSource = new FlexyPoolDataSource(configuration, connectionAcquiringStrategy);
    }

    @Test
    public void testGetConnectionWithoutCredentials() throws SQLException {
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(connectionAcquiringStrategy.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        assertSame(connection, flexyPoolDataSource.getConnection());
        assertNull(connectionRequestContextArgumentCaptor.getValue().getCredentials());
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWithCredentials() throws SQLException {
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(connectionAcquiringStrategy.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        assertSame(connection, flexyPoolDataSource.getConnection("username", "password"));
        Credentials credentials = connectionRequestContextArgumentCaptor.getValue().getCredentials();
        assertEquals("username", credentials.getUsername());
        assertEquals("password", credentials.getPassword());
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionFromTheLastStrategy() throws SQLException {

        ConnectionAcquiringStrategy otherConnectionAcquiringStrategy = Mockito.mock(ConnectionAcquiringStrategy.class);
        this.flexyPoolDataSource = new FlexyPoolDataSource(configuration,
                connectionAcquiringStrategy, otherConnectionAcquiringStrategy);

        when(connectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class))).thenThrow(new AcquireTimeoutException(new SQLException()));
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(otherConnectionAcquiringStrategy.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        assertSame(connection, flexyPoolDataSource.getConnection());
        assertNull(connectionRequestContextArgumentCaptor.getValue().getCredentials());
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWhenStrategyThrowsException() throws SQLException {

        ConnectionAcquiringStrategy otherConnectionAcquiringStrategy = Mockito.mock(ConnectionAcquiringStrategy.class);
        this.flexyPoolDataSource = new FlexyPoolDataSource(configuration,
                connectionAcquiringStrategy, otherConnectionAcquiringStrategy);

        when(connectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new AcquireTimeoutException(new SQLException()));
        when(otherConnectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new SQLException());
        try {
            flexyPoolDataSource.getConnection();
            fail("Should throw SQLException!");
        } catch (SQLException expected) {

        }
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnectionWhenNoStrategyCanAcquireConnection() throws SQLException {

        ConnectionAcquiringStrategy otherConnectionAcquiringStrategy = Mockito.mock(ConnectionAcquiringStrategy.class);
        this.flexyPoolDataSource = new FlexyPoolDataSource(configuration,
                connectionAcquiringStrategy, otherConnectionAcquiringStrategy);

        when(connectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new AcquireTimeoutException(new SQLException()));
        when(otherConnectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new AcquireTimeoutException(new SQLException()));
        try {
            flexyPoolDataSource.getConnection();
            fail("Should throw CantAcquireConnectionException!");
        } catch (CantAcquireConnectionException expected) {

        }
        verify(timer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetLogWriter() throws SQLException {
        flexyPoolDataSource.getLogWriter();
        verify(dataSource, times(1)).getLogWriter();
    }

    @Test
    public void testLogWriter() throws SQLException {
        PrintWriter out = Mockito.mock(PrintWriter.class);
        flexyPoolDataSource.setLogWriter(out);
        verify(dataSource, times(1)).setLogWriter(same(out));
    }

    @Test
    public void testGetLoginTimeout() throws SQLException {
        flexyPoolDataSource.getLoginTimeout();
        verify(dataSource, times(1)).getLoginTimeout();
    }

    @Test
    public void testSetLoginTimeout() throws SQLException {
        int seconds = 1;
        flexyPoolDataSource.setLoginTimeout(seconds);
        verify(dataSource, times(1)).setLoginTimeout(seconds);
    }

    @Test
    public void testUnwrap() throws SQLException {
        Class<?> clazz = getClass();
        flexyPoolDataSource.unwrap(clazz);
        verify(dataSource, times(1)).unwrap(same(clazz));
    }

    @Test
    public void testIsWrapperFor() throws SQLException {
        Class<?> clazz = getClass();
        flexyPoolDataSource.isWrapperFor(clazz);
        verify(dataSource, times(1)).isWrapperFor(same(clazz));
    }

    @Test
    public void testGetParentLogger() throws SQLException {
        assertEquals(Logger.getLogger(Logger.GLOBAL_LOGGER_NAME), flexyPoolDataSource.getParentLogger());
    }
}

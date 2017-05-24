package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.config.PropertyLoader;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.connection.Credentials;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.exception.CantAcquireConnectionException;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.ConnectionAcquiringStrategyFactory;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.util.JndiTestUtils;
import com.vladmihalcea.flexypool.util.MockDataSource;
import com.vladmihalcea.flexypool.util.PropertiesTestUtils;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.instanceOf;
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
    private Timer overallConnectionAcquireTimer;

    @Mock
    private Histogram concurrentConnectionCountHistogram;

    @Mock
    private Histogram concurrentConnectionRequestCountHistogram;

    @Mock
    private Timer connectionLeaseMillisTimer;

    private Configuration<DataSource> configuration;

    private FlexyPoolDataSource<DataSource> flexyPoolDataSource;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        configuration = new Configuration.Builder<DataSource>(
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
        when(metrics.timer(FlexyPoolDataSource.OVERALL_CONNECTION_ACQUIRE_MILLIS)).thenReturn(overallConnectionAcquireTimer);
        when(metrics.histogram(FlexyPoolDataSource.CONCURRENT_CONNECTIONS_HISTOGRAM)).thenReturn(concurrentConnectionCountHistogram);
        when(metrics.histogram(FlexyPoolDataSource.CONCURRENT_CONNECTION_REQUESTS_HISTOGRAM)).thenReturn(concurrentConnectionRequestCountHistogram);
        when(metrics.timer(FlexyPoolDataSource.CONNECTION_LEASE_MILLIS)).thenReturn(connectionLeaseMillisTimer);
        when(poolAdapter.getTargetDataSource()).thenReturn(dataSource);
        this.flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(configuration, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return connectionAcquiringStrategy;
            }
        });
    }

    @Test
    public void testGetConnectionWhenNoStrategy() throws SQLException {
        this.flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(configuration);
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(poolAdapter.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        verify(connection, never()).getMetaData();
        flexyPoolDataSource.getConnection().getMetaData();
        verify(connection, times(1)).getMetaData();
        assertNull(connectionRequestContextArgumentCaptor.getValue().getCredentials());
        verify(overallConnectionAcquireTimer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(1);
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(0);
    }

    @Test
    public void testGetConnectionWithoutCredentials() throws SQLException {
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(connectionAcquiringStrategy.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        verify(connection, never()).getMetaData();
        flexyPoolDataSource.getConnection().getMetaData();
        verify(connection, times(1)).getMetaData();
        assertNull(connectionRequestContextArgumentCaptor.getValue().getCredentials());
        verify(overallConnectionAcquireTimer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(1);
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(0);
    }

    @Test
    public void testGetConnectionWithCredentials() throws SQLException {
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(connectionAcquiringStrategy.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        verify(connection, never()).getMetaData();
        flexyPoolDataSource.getConnection("username", "password").getMetaData();
        verify(connection, times(1)).getMetaData();
        Credentials credentials = connectionRequestContextArgumentCaptor.getValue().getCredentials();
        assertEquals("username", credentials.getUsername());
        assertEquals("password", credentials.getPassword());
        verify(overallConnectionAcquireTimer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(1);
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(0);
    }

    @Test
    public void testGetConnectionFromTheLastStrategy() throws SQLException {

        final ConnectionAcquiringStrategy otherConnectionAcquiringStrategy = Mockito.mock(ConnectionAcquiringStrategy.class);
        this.flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(configuration, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return connectionAcquiringStrategy;
            }
        }, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return otherConnectionAcquiringStrategy;
            }
        }
        );

        when(connectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class))).thenThrow(new AcquireTimeoutException(new SQLException()));
        ArgumentCaptor<ConnectionRequestContext> connectionRequestContextArgumentCaptor
                = ArgumentCaptor.forClass(ConnectionRequestContext.class);
        when(otherConnectionAcquiringStrategy.getConnection(connectionRequestContextArgumentCaptor.capture()))
                .thenReturn(connection);
        verify(connection, never()).getMetaData();
        flexyPoolDataSource.getConnection().getMetaData();
        verify(connection, times(1)).getMetaData();
        assertNull(connectionRequestContextArgumentCaptor.getValue().getCredentials());
        verify(overallConnectionAcquireTimer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(1);
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(0);
    }

    @Test
    public void testGetConnectionWhenStrategyThrowsException() throws SQLException {

        final ConnectionAcquiringStrategy otherConnectionAcquiringStrategy = Mockito.mock(ConnectionAcquiringStrategy.class);
        this.flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(configuration, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return connectionAcquiringStrategy;
            }
        }, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return otherConnectionAcquiringStrategy;
            }
        }
        );

        when(connectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new AcquireTimeoutException(new SQLException()));
        when(otherConnectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new SQLException());
        try {
            flexyPoolDataSource.getConnection();
            fail("Should throw SQLException!");
        } catch (SQLException expected) {

        }
        verify(overallConnectionAcquireTimer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(1);
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(0);
    }

    @Test
    public void testGetConnectionWhenNoStrategyCanAcquireConnection() throws SQLException {

        final ConnectionAcquiringStrategy otherConnectionAcquiringStrategy = Mockito.mock(ConnectionAcquiringStrategy.class);
        this.flexyPoolDataSource = new FlexyPoolDataSource<DataSource>(configuration, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return connectionAcquiringStrategy;
            }
        }, new ConnectionAcquiringStrategyFactory() {
            @Override
            public ConnectionAcquiringStrategy newInstance(ConfigurationProperties configurationProperties) {
                return otherConnectionAcquiringStrategy;
            }
        }
        );

        when(connectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new AcquireTimeoutException(new SQLException()));
        when(otherConnectionAcquiringStrategy.getConnection(any(ConnectionRequestContext.class)))
                .thenThrow(new AcquireTimeoutException(new SQLException()));
        try {
            flexyPoolDataSource.getConnection();
            fail("Should throw CantAcquireConnectionException!");
        } catch (CantAcquireConnectionException expected) {

        }
        verify(overallConnectionAcquireTimer, times(1)).update(anyLong(), eq(TimeUnit.MILLISECONDS));
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(1);
        verify(concurrentConnectionRequestCountHistogram, times(1)).update(0);
    }

    @Test
    public void testAcquireReleaseConnection() throws SQLException {
        verify(concurrentConnectionCountHistogram, never()).update(anyLong());
        flexyPoolDataSource.acquireConnection();
        verify(concurrentConnectionCountHistogram, times(1)).update(1);
        flexyPoolDataSource.acquireConnection();
        flexyPoolDataSource.acquireConnection();
        verify(concurrentConnectionCountHistogram, times(1)).update(2);
        verify(concurrentConnectionCountHistogram, times(1)).update(3);
        verify(connectionLeaseMillisTimer, never()).update(anyLong(), any(TimeUnit.class));
        flexyPoolDataSource.releaseConnection(123456789L);
        verify(concurrentConnectionCountHistogram, times(2)).update(2);
        verify(connectionLeaseMillisTimer, times(1)).update(123, TimeUnit.MILLISECONDS);
        flexyPoolDataSource.releaseConnection(987654321L);
        verify(concurrentConnectionCountHistogram, times(2)).update(1);
        verify(connectionLeaseMillisTimer, times(1)).update(987, TimeUnit.MILLISECONDS);
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

    @Test
    public void testDefaultConstructorWithMissingJNDIDataSource() throws SQLException {
        try {
            PropertiesTestUtils.init();
            new FlexyPoolDataSource<DataSource>();
            fail("DataSource should be missing from JNDI");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testDefaultConstructorWithExistingJNDIDataSource() throws SQLException, IOException {
        PropertiesTestUtils.init();
        JndiTestUtils jndiTestUtils = new JndiTestUtils();
        jndiTestUtils.namingContext().bind("jdbc/DS", dataSource);
        Properties properties = new Properties();
        properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_UNIQUE_NAME.getKey(), "jdbc/DS");
        properties.put(PropertyLoader.PropertyKey.DATA_SOURCE_CLASS_NAME.getKey(), MockDataSource.class.getName());
        properties.put(PropertyLoader.PropertyKey.POOL_TIME_THRESHOLD_CONNECTION_ACQUIRE.getKey(), "-1");
        properties.put(PropertyLoader.PropertyKey.POOL_TIME_THRESHOLD_CONNECTION_LEASE.getKey(), "-1");
        PropertiesTestUtils.setProperties(properties);
        FlexyPoolDataSource<DataSource> flexyPoolDataSource = new FlexyPoolDataSource<DataSource>();
        DataSource dataSource = ((PoolAdapter) ReflectionTestUtils.getField(flexyPoolDataSource, "poolAdapter")).getTargetDataSource();
        Connection connection = Mockito.mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        flexyPoolDataSource.getConnection().close();
    }
    
    @Test
    public void testNonCloseableDataSource() throws IOException {
    	// test that we do not fail even when targetDataSource is not closeable
    	assertThat(dataSource, CoreMatchers.not(instanceOf(java.io.Closeable.class)));
    	flexyPoolDataSource.close();
    }
    
    @Test
    public void testCloseableDataSource() throws IOException {
    	DataSource ds = mock(DataSource.class, withSettings().extraInterfaces(java.io.Closeable.class));
    	
    	FlexyPoolDataSource<DataSource> fpds = new FlexyPoolDataSource<DataSource>(ds);
    	fpds.close();
    	
    	verify((java.io.Closeable)ds, times(1)).close();
    	
    }
}

package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionRequestContext;
import com.vladmihalcea.flexypool.event.ConnectionAcquisitionTimeoutEvent;
import com.vladmihalcea.flexypool.event.EventPublisher;
import com.vladmihalcea.flexypool.exception.ConnectionAcquisitionTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.Timer;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.AgroalConnectionPoolConfiguration;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AgroalPoolAdapterTest - unit test for the {@link AgroalPoolAdapter} logic, run against a real in-memory Agroal pool.
 */
public class AgroalPoolAdapterTest {

    private static final int INITIAL_MAX_SIZE = 2;
    private static final int INITIAL_MIN_SIZE = 1;

    private AgroalDataSource dataSource;

    private EventPublisher eventPublisher;

    @Before
    public void before() throws SQLException {
        dataSource = AgroalDataSource.from(
            new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration(pool -> pool
                    .minSize(INITIAL_MIN_SIZE)
                    .maxSize(INITIAL_MAX_SIZE)
                    .acquisitionTimeout(Duration.ofMillis(200))
                    .connectionFactoryConfiguration(factory -> factory
                        .jdbcUrl("jdbc:hsqldb:mem:agroalPoolAdapterTest")
                        .principal(new NamePrincipal("sa"))
                        .credential(new SimplePassword(""))
                    )
                )
        );
        eventPublisher = mock(EventPublisher.class);
    }

    @After
    public void after() {
        dataSource.close();
    }

    @Test
    public void testGetMaxPoolSize() {
        assertEquals(INITIAL_MAX_SIZE, newAdapter(false).getMaxPoolSize());
    }

    @Test
    public void testSetMaxPoolSizeResizesTheLivePool() {
        AgroalPoolAdapter adapter = newAdapter(false);

        adapter.setMaxPoolSize(5);

        assertEquals(5, adapter.getMaxPoolSize());
        assertEquals(5, poolConfiguration().maxSize());
    }

    @Test
    public void testSetMaxPoolSizeLeavesMinSizeAloneByDefault() {
        newAdapter(false).setMaxPoolSize(5);

        assertEquals(INITIAL_MIN_SIZE, poolConfiguration().minSize());
    }

    @Test
    public void testSetMaxPoolSizeKeepsPoolFixedSizeWhenConfigured() {
        newAdapter(true).setMaxPoolSize(5);

        assertEquals(5, poolConfiguration().maxSize());
        assertEquals(5, poolConfiguration().minSize());
    }

    @Test
    public void testIsTimeoutAcquisitionException() {
        AgroalPoolAdapter adapter = newAdapter(false);

        assertTrue(adapter.isTimeoutAcquisitionException(new SQLException("Sorry, acquisition timeout!")));
        assertTrue(adapter.isTimeoutAcquisitionException(new SQLException("Acquisition timeout while waiting for new connection")));
        assertTrue(adapter.isTimeoutAcquisitionException(new SQLTimeoutException()));

        assertFalse(adapter.isTimeoutAcquisitionException(new SQLException("Connection refused")));
        assertFalse(adapter.isTimeoutAcquisitionException(new SQLException()));
        assertFalse(adapter.isTimeoutAcquisitionException(new IllegalStateException("boom")));
    }

    @Test
    public void testGetConnectionTranslatesRealAgroalTimeout() throws SQLException {
        AgroalPoolAdapter adapter = newAdapter(false);
        ConnectionRequestContext context = new ConnectionRequestContext.Builder().build();

        Connection first = adapter.getConnection(context);
        Connection second = adapter.getConnection(context);
        try {
            // the pool is exhausted (max size 2), so Agroal will time out
            adapter.getConnection(context);
            fail("Expected an acquisition timeout");
        } catch (ConnectionAcquisitionTimeoutException expected) {
            verify(eventPublisher).publish(any(ConnectionAcquisitionTimeoutEvent.class));
        } finally {
            first.close();
            second.close();
        }
    }

    @Test
    public void testGetConnectionSucceedsAfterResizeFreesCapacity() throws SQLException {
        AgroalPoolAdapter adapter = newAdapter(false);
        ConnectionRequestContext context = new ConnectionRequestContext.Builder().build();

        Connection first = adapter.getConnection(context);
        Connection second = adapter.getConnection(context);
        adapter.setMaxPoolSize(3);
        Connection third = adapter.getConnection(context);

        assertNotNull(third);
        first.close();
        second.close();
        third.close();
    }

    private AgroalConnectionPoolConfiguration poolConfiguration() {
        return dataSource.getConfiguration().connectionPoolConfiguration();
    }

    @SuppressWarnings("unchecked")
    private AgroalPoolAdapter newAdapter(boolean maintainFixedSizePool) {
        Metrics metrics = mock(Metrics.class);
        when(metrics.timer(AbstractPoolAdapter.CONNECTION_ACQUISITION_MILLIS)).thenReturn(mock(Timer.class));

        ConfigurationProperties<AgroalDataSource, Metrics, PoolAdapter<AgroalDataSource>> properties =
            mock(ConfigurationProperties.class);
        when(properties.getTargetDataSource()).thenReturn(dataSource);
        when(properties.getMetrics()).thenReturn(metrics);
        when(properties.getEventPublisher()).thenReturn(eventPublisher);
        when(properties.getUniqueName()).thenReturn("agroalPoolAdapterTest");
        when(properties.isMaintainFixedSizePool()).thenReturn(maintainFixedSizePool);

        return new AgroalPoolAdapter(properties);
    }
}

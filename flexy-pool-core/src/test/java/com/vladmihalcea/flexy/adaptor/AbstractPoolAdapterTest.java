package com.vladmihalcea.flexy.adaptor;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.connection.ConnectionRequestContext;
import com.vladmihalcea.flexy.context.Context;
import com.vladmihalcea.flexy.metric.Metrics;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

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

    private Context context;

    private ConnectionRequestContext connectionRequestContext;

    private AbstractPoolAdapter<DataSource> abstractPoolAdapter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Configuration configuration = new Configuration(UUID.randomUUID().toString());
        context = new Context(configuration, metrics);
        connectionRequestContext = new ConnectionRequestContext.Builder().build();
        abstractPoolAdapter = new TestPoolAdaptor(context, dataSource);
    }

    @Test
    public void testGetConnectionWithoutCredentials() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        assertSame(connection, abstractPoolAdapter.getConnection(connectionRequestContext));
    }
}

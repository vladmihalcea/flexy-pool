package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.adaptor.ViburDBCPPoolAdapter;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.vibur.dbcp.ViburDBCPDataSource;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * ViburDBCPIntegrationTest - ViburDBCPDataSource Integration Test
 *
 * @author Vlad Mihalcea
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ViburDBCPIntegrationTest extends AbstractPoolAdapterIntegrationTest {

    @Autowired
    private ViburDBCPDataSource poolingDataSource;

    @Override
    protected void verifyLeasedConnections(List<Connection> leasedConnections) {
        assertEquals(3, leasedConnections.size());
    }

    @Test
    public void testMaxPollSize() {
        ConfigurationProperties configurationProperties = Mockito.mock(ConfigurationProperties.class);
        Metrics metrics = Mockito.mock(Metrics.class);
        when(configurationProperties.getMetrics()).thenReturn(metrics);

        when(configurationProperties.getTargetDataSource()).thenReturn(poolingDataSource);
        when(metrics.timer(anyString())).thenReturn(null);
        @SuppressWarnings("unchecked")
        ViburDBCPPoolAdapter poolAdapter = new ViburDBCPPoolAdapter(configurationProperties);
        assertSame(3, poolAdapter.getMaxPoolSize());
        try {
            poolAdapter.setMaxPoolSize(12);
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }
}

package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.exception.CantAcquireConnectionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * AbstractPoolAdapterIntegrationTest - Abstract Pool Adapter Integration Test
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractPoolAdapterIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPoolAdapterIntegrationTest.class);

    @Resource
    private DataSource dataSource;

    @Test
    public void test() throws SQLException {
        int index = 0;
        List<Connection> leasedConnections = new ArrayList<Connection>();

        try {
            for(;;++index) {
                leasedConnections.add(getConnection(index));
            }
        } catch (SQLException e) {
            assertTrue(e instanceof CantAcquireConnectionException);
            verifyLeasedConnections(leasedConnections);
        }
    }

    private Connection getConnection(int index) throws SQLException {
        LOGGER.info("Try to get connection {}", index);
        Connection connection = dataSource.getConnection();
        assertNotNull(connection);
        LOGGER.info("Got connection {}", connection);
        return connection;
    }

    protected void verifyLeasedConnections(List<Connection> leasedConnections) {
        assertEquals(5, leasedConnections.size());
    }
}

package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.exception.CantAcquireConnectionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * AbstractPoolAdapterIntegrationTest - Abstract Pool Adapter Integration Test
 *
 * @author Vlad Mihalcea
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractPoolAdapterIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPoolAdapterIntegrationTest.class);

    @Resource
    private FlexyPoolDataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    @Test
    public void test() throws SQLException {
        int index = 0;
        List<Connection> leasedConnections = new ArrayList<Connection>();

        try {
            for(;hasMoreConnections(index);++index) {
                try {
                    Connection connection = getConnection(index);
                    leasedConnections.add(connection);
                } catch (UnsupportedOperationException e) {
                    LOGGER.info("DataSource doesn't support adjusting pool size", e);
                }
            }
        } catch (SQLException e) {
            assertTrue(e instanceof CantAcquireConnectionException);
            verifyLeasedConnections(leasedConnections);
        } finally {
            closeConnection(leasedConnections);
        }
    }

    protected boolean hasMoreConnections(int index) {
       return true;
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

    protected void closeConnection(List<Connection> leasedConnections) {
        for(Connection leasedConnection : leasedConnections) {
            try {
                leasedConnection.close();
            } catch (SQLException e) {
                fail(e.getMessage());
            }
        }
    }
}

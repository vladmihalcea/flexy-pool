package com.vladmihalcea.flexypool;

import com.vladmihalcea.flexypool.exception.CantAcquireConnectionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * BitronixIntegrationTest - Bitronix Integration Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BitronixIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitronixIntegrationTest.class);

    @Resource
    private DataSource dataSource;

    @Test
    public void test() throws SQLException {
        int index = 0;
        getConnection(index++);
        getConnection(index++);
        getConnection(index++);
        getConnection(index++);
        getConnection(index++);
        try {
            getConnection(index++);
            fail();
        } catch (CantAcquireConnectionException expected) {

        }
    }

    private Connection getConnection(int index) throws SQLException {
        LOGGER.info("Try to get connection {}", index);
        Connection connection = dataSource.getConnection();
        assertNotNull(connection);
        LOGGER.info("Got connection {}", connection);
        return connection;
    }
}

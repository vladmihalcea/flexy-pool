package com.vladmihalcea.flexypool;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vibur.dbcp.ViburDBCPDataSource;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * ViburDBCPIntegrationTest - ViburDBCPDataSource Integration Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-test.xml")
public class ViburDBCPIntegrationTest extends AbstractPoolAdapterIntegrationTest {

    @Override
    protected void verifyLeasedConnections(List<Connection> leasedConnections) {
        assertEquals(3, leasedConnections.size());
    }
}

package com.vladmihalcea.flexypool;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * DataSourceIntegrationTest - DataSource Integration Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-test.xml")
public class DataSourceIntegrationTest extends AbstractPoolAdapterIntegrationTest {

    @Override
    protected void verifyLeasedConnections(List<Connection> leasedConnections) {
        assertEquals(5, leasedConnections.size());
    }

    protected boolean hasMoreConnections(int index) {
        return index < 5;
    }
}

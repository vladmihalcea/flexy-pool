package com.vladmihalcea.flexypool;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        //ViburDBCPDataSource#setPoolMaxSize requires a pool restart to be taken into consideration
        //This will end up recreating teh data source, so the old connections are not managed anymore
        //Because we have 2 overflows (from 3 to 4 and from 4 to 5) we will end up with
        //12 connections = 3(initial max) + 4(initial max + 1 over flow) + 5(initial max + 2 over flow)
        assertEquals(12, leasedConnections.size());
    }
}

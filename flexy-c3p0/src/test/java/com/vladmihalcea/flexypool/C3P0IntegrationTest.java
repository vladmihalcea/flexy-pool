package com.vladmihalcea.flexypool;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * C3P0IntegrationTest - BasicDataSource Integration Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class C3P0IntegrationTest extends AbstractPoolAdapterIntegrationTest {

    @Override
    protected void verifyLeasedConnections(List<Connection> leasedConnections) {
        //ComboPooledDataSource#setMaxPoolSize calls resetPoolManager( false );
        //This will end up recreating the data source, so the old connections are not managed anymore
        //Because we have 2 overflows (from 3 to 4 and from 4 to 5) we will end up with
        //12 connections = 3(initial max) + 4(initial max + 1 over flow) + 5(initial max + 2 over flow)

        //ComboPooledDataSource doesn't initializes eagerly, so we can get a timeout right from teh first call, because
        //the first connection request might have to wait for the pool to be initialised
        //This will end up recreating the data source from the first call.
        //Because we have 2 overflows (from 3 to 4 and from 4 to 5) we will end up with
        //9 connections = 0(initial call) + 4(initial max + 1 over flow) + 5(initial max + 2 over flow)
        int leasedConnectionCount = leasedConnections.size();
        if( !(leasedConnectionCount == 12 || leasedConnectionCount == 9) ) {
            LOGGER.error( "Unexpected leased connection count {}", leasedConnectionCount );
        }
    }
}

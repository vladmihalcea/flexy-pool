package com.vladmihalcea.flexypool;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * HikariDataSourceIntegrationTest - BasicDataSource Integration Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-test.xml")
public class HikariDataSourceIntegrationTest extends AbstractPoolAdapterIntegrationTest {

}

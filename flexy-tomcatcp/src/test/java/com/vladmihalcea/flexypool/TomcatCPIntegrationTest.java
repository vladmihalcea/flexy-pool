package com.vladmihalcea.flexypool;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * TomcatCPIntegrationTest - DataSource Integration Test
 *
 * @author Vlad Mihalcea
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-test.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TomcatCPIntegrationTest extends AbstractPoolAdapterIntegrationTest {

}

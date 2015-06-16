package com.vladmihalcea.flexypool.adaptor;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ResourceLocalFlexyPoolConnectionProviderTest - FlexyPoolConnectionProvider Test for RESOURCE_LOCAL Environment
 *
 * @author Vlad Mihalcea
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-local-tx.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ResourceLocalFlexyPoolHibernateConnectionProviderIntegrationTest
        extends AbstractFlexyPoolHibernateConnectionProviderIntegrationTest {
}
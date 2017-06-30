package com.vladmihalcea.flexypool.adaptor;

import org.hibernate.Session;
import org.hibernate.internal.SessionFactoryImpl;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import static org.junit.Assert.assertEquals;

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

	@Test
	public void testOverrideProperties() {
		getTransactionTemplate().execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				Session session = getEntityManager().unwrap( Session.class );
				SessionFactoryImpl sessionFactory = (SessionFactoryImpl) session.getSessionFactory();
				FlexyPoolHibernateConnectionProvider flexyPoolHibernateConnectionProvider =
						(FlexyPoolHibernateConnectionProvider) sessionFactory.getConnectionProvider();
				FlexyPoolDataSource flexyPoolDataSource = ReflectionUtils.getFieldValue(
						flexyPoolHibernateConnectionProvider,
						"flexyPoolDataSource"
				);
				assertEquals( "abcd1234", ReflectionUtils.getFieldValue( flexyPoolDataSource, "uniqueName" ));

				return null;
			}
		});

	}
}
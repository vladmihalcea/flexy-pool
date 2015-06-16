package com.vladmihalcea.flexypool.adaptor.glassfish;

import com.vladmihalcea.flexypool.adaptor.glassfish.datasource.DefaultDataSourceConfiguration;
import com.vladmihalcea.flexypool.adaptor.glassfish.datasource.FlexyPoolDataSourceConfiguration;
import com.vladmihalcea.flexypool.model.Book;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class GlassfishFlexyPoolDataSourceJndiIntegrationTest extends AbstractGlassfishIntegrationTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addPackage(Book.class.getPackage())
            .addClasses(DefaultDataSourceConfiguration.class, FlexyPoolDataSourceConfiguration.class)
            .addAsManifestResource("data-source-auto-create/test-persistence-data-source-auto-create.xml", "persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("data-source-jndi/flexy-pool-create-data-source-jndi.properties", "flexy-pool.properties");
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}

package com.vladmihalcea.flexypool.adaptor.tomee;

import com.vladmihalcea.flexypool.adaptor.tomee.datasource.DefaultDataSourceConfiguration;
import com.vladmihalcea.flexypool.adaptor.tomee.datasource.FlexyPoolDataSourceConfiguration;
import com.vladmihalcea.flexypool.config.PropertyLoader;
import com.vladmihalcea.flexypool.model.Book;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class TomeeFlexyPoolDataSourceJndiIntegrationTest extends AbstractJavaEEIntegrationTest {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Deployment
    public static Archive<?> createDeployment() {
        System.setProperty(PropertyLoader.PROPERTIES_FILE_PATH, "data-source-jndi/flexy-pool-create-data-source-jndi.properties");
        return ShrinkWrap.create(JavaArchive.class)
            .addPackage(Book.class.getPackage())
            .addClasses(DefaultDataSourceConfiguration.class, FlexyPoolDataSourceConfiguration.class)
            .addAsManifestResource("data-source-auto-create/test-persistence-data-source-auto-create.xml", "persistence.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Override
    protected EntityManager newEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}

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

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

public class TomeeFlexyPoolDataSourceAutoCreateIntegrationTest extends AbstractJavaEEIntegrationTest {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Resource(lookup = "java:global/jdbc/flexypool")
    private DataSource dataSource;

    @Deployment
    public static Archive<?> createDeployment() {
        System.setProperty(PropertyLoader.PROPERTIES_FILE_PATH, "data-source-auto-create/flexy-pool-data-source-auto-create.properties");
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

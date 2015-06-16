package com.vladmihalcea.flexypool.adaptor.tomee;

import com.vladmihalcea.flexypool.model.Book;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@TransactionManagement(TransactionManagementType.BEAN)
public abstract class AbstractJavaEEIntegrationTest {

    private EntityManager entityManager;

    @Before
    public void init() {
        entityManager = newEntityManager();
        doInTransaction(new VoidCallable() {
            @Override
            public void call() {
                entityManager.createQuery("delete from Book").executeUpdate();
            }
        });
    }

    @After
    public void destroy() {
        entityManager.close();
    }

    @Test
    public void addBook() throws Exception {
        Book book = doInTransaction(new Callable<Book>() {
            @Override
            public Book call() {
                Book book = new Book();
                book.setId(1L);
                book.setName("High-Performance Java Persistence");
                entityManager.persist(book);
                return book;
            }
        });
        MBeanInfo connectionLeaseMillisMBean = connectionLeaseMillisMBean();
        assertNotNull(connectionLeaseMillisMBean);
    }

    private <V> V doInTransaction(Callable<V> callable) {
        V result;
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            result = callable.call();
            entityTransaction.commit();
            return result;
        } catch (Exception e) {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            throw new IllegalStateException(e);
        }
    }

    private void doInTransaction(VoidCallable callable) {
        EntityTransaction entityTransaction = null;
        try {
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            callable.call();
            entityTransaction.commit();
        } catch (Exception e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            throw new IllegalStateException(e);
        }
    }

    public interface Callable<V> {
        V call();
    }

    public interface VoidCallable {
        void call();
    }

    private MBeanInfo connectionLeaseMillisMBean() {
        try {
            ObjectName objectName = new ObjectName("com.vladmihalcea.flexypool.metric.codahale.JmxMetricReporter.unique-name:name=connectionLeaseMillis");
            return ManagementFactory.getPlatformMBeanServer().getMBeanInfo(objectName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    protected abstract EntityManager newEntityManager();

}

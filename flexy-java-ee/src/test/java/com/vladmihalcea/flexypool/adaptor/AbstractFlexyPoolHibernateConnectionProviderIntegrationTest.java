package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.model.Book;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * AbstractFlexyPoolHibernateConnectionProviderIntegrationTest - FlexyPoolConnectionProvider Integration Test for Java EE Environment
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractFlexyPoolHibernateConnectionProviderIntegrationTest {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MockMetricsFactory metricsFactory;

    @Before
    public void init() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                entityManager.createQuery("delete from Book").executeUpdate();
                return null;
            }
        });
    }

    @Test
    public void test() {
        final Book book = transactionTemplate.execute(new TransactionCallback<Book>() {
            @Override
            public Book doInTransaction(TransactionStatus status) {
                Book book = new Book();
                book.setId(1L);
                book.setName("High-Performance Java Persistence");
                entityManager.persist(book);
                return book;
            }
        });

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                assertEquals(book.getName(), entityManager.find(Book.class, book.getId()).getName());
                return null;
            }
        });
        verify(metricsFactory.getConcurrentConnectionRequestCountHistogram(), atLeastOnce()).update(1);
        verify(metricsFactory.getConcurrentConnectionRequestCountHistogram(), atLeastOnce()).update(0);
    }
}
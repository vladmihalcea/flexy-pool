/*
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.model.Book;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.decorator.Decorator;
import javax.inject.Inject;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public abstract class AbstractGlassfishIntegrationTest {

    protected abstract EntityManager getEntityManager();
    
    @Inject
    private UserTransaction userTransaction;

    @Before
    public void init() throws Exception {
        doInTransaction(new VoidCallable() {
            @Override
            public void call() {
                getEntityManager().createQuery("delete from Book").executeUpdate();
            }
        });
    }

    @Test
    public void addBook() throws Exception {
        Book book = doInTransaction(new Callable<Book>() {
            @Override
            public Book call() {
                Book book = new Book();
                book.setId(1L);
                book.setName("High-Performance Java Persistence");
                getEntityManager().persist(book);
                return book;
            }
        });
        MBeanInfo connectionLeaseMillisMBean = connectionLeaseMillisMBean();
        assertNotNull(connectionLeaseMillisMBean);
    }

    private <V> V doInTransaction(Callable<V> callable) {
        V result;
        try {
            userTransaction.begin();
            getEntityManager().joinTransaction();
            result = callable.call();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                throw new IllegalStateException(e);
            }
            throw new IllegalStateException(e);
        }
        return result;
    }

    private void doInTransaction(VoidCallable callable) {
        try {
            userTransaction.begin();
            getEntityManager().joinTransaction();
            callable.call();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                throw new IllegalStateException(e);
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
            ObjectName objectName = new ObjectName("com.vladmihalcea.flexypool.metric.codahale.JmxMetricReporter.jdbc/arquillian:name=connectionLeaseMillis");
            return ManagementFactory.getPlatformMBeanServer().getMBeanInfo(objectName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}

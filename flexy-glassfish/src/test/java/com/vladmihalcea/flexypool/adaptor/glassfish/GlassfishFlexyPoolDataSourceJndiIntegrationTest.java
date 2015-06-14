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

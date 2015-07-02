package com.vladmihalcea.flexypool.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * ClassLoaderUtilsTest - ClassLoaderUtils Test
 *
 * @author Vlad Mihalcea
 */
public class ClassLoaderUtilsTest extends AbstractUtilsTest<ClassLoaderUtils> {

    @Test
    public void testGetClassLoader() {
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader();
        assertSame(Thread.currentThread().getContextClassLoader(), classLoader);
        try {
            Thread.currentThread().setContextClassLoader(null);
            assertSame(ClassLoaderUtils.class.getClassLoader(), ClassLoaderUtils.getClassLoader());
        } finally {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    @Test
    public void testLoadClass() {
        try {
            ClassLoaderUtils.loadClass(ClassLoaderUtilsTest.class.getName());
        } catch (ClassNotFoundException e) {
            fail(e.getMessage());
        }
        try {
            ClassLoaderUtils.loadClass("org.abc.Def");
            fail("Should throw ClassNotFoundException!");
        } catch (ClassNotFoundException expected) {
        }
    }

    @Test
    public void testFindClass() {
        assertTrue(ClassLoaderUtils.findClass(ClassLoaderUtilsTest.class.getName()));
        assertFalse(ClassLoaderUtils.findClass("org.abc.Def"));
    }

    @Test
    public void testGetResource() {
        assertNotNull(ClassLoaderUtils.getResource("META-INF/services/com.vladmihalcea.flexypool.metric.MetricsFactoryService"));
        assertNull(ClassLoaderUtils.getResource("META-INF/no.file"));
    }

    @Test
    public void testGetResourceAsStream() {
        try {
            ClassLoaderUtils.getResourceAsStream("META-INF/services/com.vladmihalcea.flexypool.metric.MetricsFactoryService").close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Override
    protected Class<ClassLoaderUtils> getUtilsClass() {
        return ClassLoaderUtils.class;
    }
}
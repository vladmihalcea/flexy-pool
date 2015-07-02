package com.vladmihalcea.flexypool.util;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * ClassLoaderUtilsTest - ClassLoaderUtils Test
 *
 * @author Vlad Mihalcea
 */
public class ClassLoaderUtilsTest {

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
    }

    @Test
    public void testFindClass() {

    }

    @Test
    public void testGetResource() {

    }

    @Test
    public void testGetResourceAsStream() {

    }
}
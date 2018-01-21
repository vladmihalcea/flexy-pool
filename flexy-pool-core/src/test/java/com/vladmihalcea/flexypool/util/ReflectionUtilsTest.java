package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.exception.ReflectionException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * ReflectionUtilsTest - ReflectionUtils Test
 *
 * @author Vlad Mihalcea
 */
public class ReflectionUtilsTest extends AbstractUtilsTest<ReflectionUtils> {

    @Test
    public void testGetFieldValue() {
        TestObject testObject = new TestObject();
        assertEquals("testObject", ReflectionUtils.getFieldValue(testObject, "name"));
        assertEquals(1, ((Number) ReflectionUtils.getFieldValue(testObject, "version")).intValue());
    }

    @Test(expected = ReflectionException.class)
    public void testGetFieldValueThrowsReflectionException() {
        ReflectionUtils.getFieldValue(new TestObject(), "unknown");
    }

    @Test
    public void testSetFieldValue() {
        TestObject testObject = new TestObject();
        assertEquals("testObject", ReflectionUtils.getFieldValue(testObject, "name"));
        ReflectionUtils.setFieldValue(testObject, "name", "testObjectNameChanged");
        assertEquals("testObjectNameChanged", ReflectionUtils.getFieldValue(testObject, "name"));
        ReflectionUtils.setFieldValue(testObject, "name", null);
        assertEquals(null, ReflectionUtils.getFieldValue(testObject, "name"));
    }

    @Test(expected = ReflectionException.class)
    public void testSetFieldValueThrowsReflectionException() {
        ReflectionUtils.setFieldValue(new TestObject(), "unknown", "value");
    }

    @Test
    public void testGetMethod() {
        assertNotNull(ReflectionUtils.getMethod(new TestObject(), "getName"));
    }

    @Test(expected = ReflectionException.class)
    public void testGetMethodThrowsReflectionException() {
        ReflectionUtils.getMethod(new TestObject(), "unknown");
    }

    @Test
    public void testHasMethod() {
        assertTrue(ReflectionUtils.hasMethod(TestObject.class, "getName"));
    }

    @Test
    public void testHasMethodThrowsReflectionException() {
        assertFalse(ReflectionUtils.hasMethod(TestObject.class, "unknown"));
    }

    @Test
    public void testInvoke() {
        TestObject testObject = new TestObject();
        assertEquals("testObject", ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "getName")));
        ReflectionUtils.setFieldValue(testObject, "name", "testObjectNameChanged");
        assertEquals("testObjectNameChanged", ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "getName")));
        assertNull(ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "start")));
        try {
            ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "setFails"));
            fail("Should have failed!");
        } catch (ReflectionException e) {
            assertEquals(InvocationTargetException.class, e.getCause().getClass());
            assertEquals(IllegalArgumentException.class, e.getCause().getCause().getClass());
        }
    }

    @Test
    public void testGetSetter() {
        TestObject testObject = new TestObject();
        try {
            ReflectionUtils.getSetter(testObject, "name", String.class);
            fail("There is no setName in TestObject");
        } catch (ReflectionException expected) {
            assertEquals(NoSuchMethodException.class, expected.getCause().getClass());
        }
        assertNotNull(ReflectionUtils.getSetter(testObject, "version", Integer.class));
        try {
            ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "setFails"));
            fail("Should have failed!");
        } catch (ReflectionException e) {
            assertEquals(InvocationTargetException.class, e.getCause().getClass());
            assertEquals(IllegalArgumentException.class, e.getCause().getCause().getClass());
        }
    }

    @Test
    public void testInvokeSetter() {
        TestObject testObject = new TestObject();
        ReflectionUtils.invokeSetter(testObject, "version", 12);
        assertEquals(12, (int) ReflectionUtils.getFieldValue(testObject, "version"));
    }

    @Override
    protected Class<ReflectionUtils> getUtilsClass() {
        return ReflectionUtils.class;
    }
}

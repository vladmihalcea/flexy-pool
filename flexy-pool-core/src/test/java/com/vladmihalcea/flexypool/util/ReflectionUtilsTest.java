package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.exception.ReflectionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * ReflectionUtilsTest - ReflectionUtils Test
 *
 * @author Vlad Mihalcea
 */
public class ReflectionUtilsTest {

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
    public void testInvoke() {
        TestObject testObject = new TestObject();
        assertEquals("testObject", ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "getName")));
        ReflectionUtils.setFieldValue(testObject, "name", "testObjectNameChanged");
        assertEquals("testObjectNameChanged", ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "getName")));
        assertNull(ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "start")));
    }
}

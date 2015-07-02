package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.exception.ReflectionException;
import org.junit.Test;

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
    public void testInvoke() {
        TestObject testObject = new TestObject();
        assertEquals("testObject", ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "getName")));
        ReflectionUtils.setFieldValue(testObject, "name", "testObjectNameChanged");
        assertEquals("testObjectNameChanged", ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "getName")));
        assertNull(ReflectionUtils.invoke(testObject, ReflectionUtils.getMethod(testObject, "start")));
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
    }

    @Test
    public void testInvokeSetter() {
        TestObject testObject = new TestObject();
        ReflectionUtils.invokeSetter(testObject, "version", 12);
        assertEquals(12, ReflectionUtils.getFieldValue(testObject, "version"));
    }

    @Override
    protected Class<ReflectionUtils> getUtilsClass() {
        return ReflectionUtils.class;
    }
}

package com.vladmihalcea.flexypool.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * AbstractUtilsTest - Base Utils Test
 *
 * @author Vlad MIhalcea
 */
public abstract class AbstractUtilsTest<T> {

    /**
     * Get the current utilities class
     * @return utilities class
     */
    protected abstract Class<T> getUtilsClass();

    @Test
    public void testInstantiate() {
        Class<T> utilsClass = getUtilsClass();
        assertNotNull(utilsClass);
        try {
            Constructor<T> testingClassConstructor = null;
            @SuppressWarnings("unchecked")
            Constructor<T>[] testingClassConstructors = (Constructor<T>[]) utilsClass.getDeclaredConstructors();
            for(Constructor<T> constructor : testingClassConstructors) {
                if(constructor.getParameterTypes().length == 0) {
                    testingClassConstructor = constructor;
                    break;
                }
            }
            if(testingClassConstructor == null) {
                fail("Could not find any default constructor in " + utilsClass);
            }
            testingClassConstructor.setAccessible(true);
            testingClassConstructor.newInstance();
            fail("The " + utilsClass + " should not be instantiated!");
        } catch (Exception expected) {
            assertEquals(expected.getClass(), InvocationTargetException.class);
            @SuppressWarnings("unchecked")
            InvocationTargetException exception = (InvocationTargetException) expected;
            assertEquals(UnsupportedOperationException.class, exception.getTargetException().getClass());
        }
    }
}

package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.exception.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <code>ReflectionUtils</code> defines reflection utilities.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        throw new UnsupportedOperationException("ReflectionUtils is not instantiable!");
    }

    /**
     * Get target object field value
     * @param target target object
     * @param fieldName field name
     * @param <T> field type
     * @return field value
     */
    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            T returnValue = (T) field.get(target);
            return returnValue;
        } catch (NoSuchFieldException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Set target object field value
     * @param target target object
     * @param fieldName field name
     * @param value field value
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Get target method
     * @param target  target object
     * @param methodName method name
     * @param parameterTypes method parameter types
     * @return return value
     */
    public static Method getMethod(Object target, String methodName, Class... parameterTypes) {
        try {
            return target.getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Invoke target method
     * @param method method to invoke
     * @param parameters method parameters
     * @return return value
     */
    public static <T> T invoke(Object target, Method method, Object... parameters) {
        try {
            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(target, parameters);
            return returnValue;
        } catch (InvocationTargetException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }
}

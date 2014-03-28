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

    public static class TypedObject {
        private Class<?> type;
        private Object object;

        public TypedObject(Class<?> type, Object object) {
            this.type = type;
            this.object = object;
        }

        public Class<?> getType() {
            return type;
        }

        public Object getObject() {
            return object;
        }
    }

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
     * Invoke target method
     * @param target  target object
     * @param methodName method name
     * @param parameterTypedObjects method parameters
     * @return return value
     */
    public static <T> T invoke(Object target, String methodName, TypedObject... parameterTypedObjects) {
        try {
            Class<?>[] parameterTypes = new Class[parameterTypedObjects.length];
            Object[] parameters = new Object[parameterTypedObjects.length];
            for (int i = 0; i < parameterTypedObjects.length; i++) {
                TypedObject parameterTypedObject = parameterTypedObjects[i];
                parameterTypes[i] = parameterTypedObject.getType();
                parameters[i] = parameterTypedObject.getObject();
            }
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(target, parameters);
            return returnValue;
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }
}

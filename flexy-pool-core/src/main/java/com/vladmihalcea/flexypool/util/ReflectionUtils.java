package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.exception.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * <code>ReflectionUtils</code> defines reflection utilities.
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
 * @since 1.0
 */
public final class ReflectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    public static final String SETTER_PREFIX = "set";

    private ReflectionUtils() {
        throw new UnsupportedOperationException("ReflectionUtils is not instantiable!");
    }

    /**
     * Get target object field value
     *
     * @param target    target object
     * @param fieldName field name
     * @param <T>       field type
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
            throw handleException(fieldName, e);
        } catch (IllegalAccessException e) {
            throw handleException(fieldName, e);
        }
    }

    /**
     * Set target object field value
     *
     * @param target    target object
     * @param fieldName field name
     * @param value     field value
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            throw handleException(fieldName, e);
        } catch (IllegalAccessException e) {
            throw handleException(fieldName, e);
        }
    }

    /**
     * Get target method
     *
     * @param target         target object
     * @param methodName     method name
     * @param parameterTypes method parameter types
     * @return return value
     */
    public static Method getMethod(Object target, String methodName, Class... parameterTypes) {
        try {
            return target.getClass().getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw handleException(methodName, e);
        }
    }

    /**
     * Get setter method
     *
     * @param target        target object
     * @param property      property
     * @param parameterType setter parameter type
     * @return setter method
     */
    public static Method getSetter(Object target, String property, Class<?> parameterType) {
        String setterMethodName = SETTER_PREFIX + property.substring(0, 1).toUpperCase() + property.substring(1);
        return getMethod(target, setterMethodName, parameterType);
    }

    /**
     * Invoke target method
     *
     * @param method     method to invoke
     * @param parameters method parameters
     * @return return value
     */
    public static <T> T invoke(Object target, Method method, Object... parameters) {
        try {
            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(target, parameters);
            return returnValue;
        } catch (InvocationTargetException e) {
            throw handleException(method.getName(), e);
        } catch (IllegalAccessException e) {
            throw handleException(method.getName(), e);
        }
    }

    /**
     * Invoke setter method with the given parameter
     *
     * @param target    target object
     * @param property  property
     * @param parameter setter parameter
     */
    public static void invokeSetter(Object target, String property, Object parameter) {
        Method setter = getSetter(target, property, parameter.getClass());
        try {
            setter.invoke(target, parameter);
        } catch (IllegalAccessException e) {
            throw handleException(setter.getName(), e);
        } catch (InvocationTargetException e) {
            throw handleException(setter.getName(), e);
        }
    }

    /**
     * Handle {@link NoSuchFieldException} by logging it and rethrown it as a {@link ReflectionException}
     *
     * @param fieldName field name
     * @param e         exception
     * @return wrapped exception
     */
    private static ReflectionException handleException(String fieldName, NoSuchFieldException e) {
        LOGGER.error("Couldn't find field " + fieldName, e);
        return new ReflectionException(e);
    }

    /**
     * Handle {@link NoSuchMethodException} by logging it and rethrown it as a {@link ReflectionException}
     *
     * @param methodName method name
     * @param e          exception
     * @return wrapped exception
     */
    private static ReflectionException handleException(String methodName, NoSuchMethodException e) {
        LOGGER.error("Couldn't find method " + methodName, e);
        return new ReflectionException(e);
    }

    /**
     * Handle {@link IllegalAccessException} by logging it and rethrown it as a {@link ReflectionException}
     *
     * @param memberName member name
     * @param e          exception
     * @return wrapped exception
     */
    private static ReflectionException handleException(String memberName, IllegalAccessException e) {
        LOGGER.error("Couldn't access member " + memberName, e);
        return new ReflectionException(e);
    }

    /**
     * Handle {@link InvocationTargetException} by logging it and rethrown it as a {@link ReflectionException}
     *
     * @param methodName method name
     * @param e          exception
     * @return wrapped exception
     */
    private static ReflectionException handleException(String methodName, InvocationTargetException e) {
        LOGGER.error("Couldn't invoke method " + methodName, e);
        return new ReflectionException(e);
    }
}

package com.vladmihalcea.flexypool.util;

/**
 * ClassLoaderUtils - Class loading utilities.
 *
 * @author Vlad Mihalcea
 */
public class ClassLoaderUtils {

    private ClassLoaderUtils() {
        throw new UnsupportedOperationException("ClassLoaderUtils is not instantiable!");
    }

    /**
     * Load the available ClassLoader
     * @return ClassLoader
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return (classLoader != null) ? classLoader : ClassLoaderUtils.class.getClassLoader();
    }

    /**
     * Load the Class denoted by the given string representation
     * @param className class string representation
     * @return Class
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String className) throws ClassNotFoundException {
        return (Class<T>) getClassLoader().loadClass(className);
    }
}

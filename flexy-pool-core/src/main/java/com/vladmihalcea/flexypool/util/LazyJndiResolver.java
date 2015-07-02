package com.vladmihalcea.flexypool.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <code>LazyJndiResolver</code> - Resolves Jndi resources lazily
 *
 * @author Vlad Mihalcea
 * @since 1.2
 */
public final class LazyJndiResolver implements InvocationHandler {

    private final String name;

    private Object target;

    private LazyJndiResolver() {
        throw new UnsupportedOperationException("ReflectionUtils is not instantiable!");
    }

    /**
     * The JNDI name of the associated object
     * @param name JNDI name of the associated object
     */
    private LazyJndiResolver(String name) {
        this.name = name;
    }

    /**
     * Resolves the JNDI object upon invoking any method on the associated Proxy
     * @param proxy proxy
     * @param method method
     * @param args arguments
     * @return return value
     * @throws Throwable in case of failures
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(target == null) {
            target = JndiUtils.lookup(name);
        }
        return method.invoke(target, args);
    }

    /**
     * Creates a new Proxy instance
     * @param name JNDI name of the object to be lazily looked up
     * @param objectType object type
     * @param <T> typed parameter
     * @return Proxy object
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String name, Class<?> objectType) {
        return (T) Proxy.newProxyInstance(
                ClassLoaderUtils.getClassLoader(),
                new Class[]{objectType},
                new LazyJndiResolver(name));
    }

}

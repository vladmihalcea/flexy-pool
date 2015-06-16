package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.util.ClassLoaderUtils;
import com.vladmihalcea.flexypool.util.JndiUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * LazyJndiDataSource -
 *
 * @author Vlad Mihalcea
 */
public class LazyJndiResolver implements InvocationHandler {

    private final String name;

    private Object target;

    private LazyJndiResolver(String name) {
        this.name = name;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(target == null) {
            target = JndiUtils.lookup(name);
        }
        return method.invoke(target, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String name, Class<?> objectType) {
        return (T) Proxy.newProxyInstance(
                ClassLoaderUtils.getClassLoader(),
                new Class[]{objectType},
                new LazyJndiResolver(name));
    }

}

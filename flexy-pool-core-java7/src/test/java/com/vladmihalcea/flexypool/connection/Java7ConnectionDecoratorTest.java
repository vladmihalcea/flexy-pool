package com.vladmihalcea.flexypool.connection;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java7ManagedConnectionTest - Java7ManagedConnection Test
 *
 * @author Vlad Mihalcea
 */
public class Java7ConnectionDecoratorTest extends ConnectionDecoratorTest {

    @Override
    protected List<Method> getConnectionMethods(ConnectionDecorator connectionDecorator) {
        List<Method> methods = new ArrayList<Method>(Arrays.asList(connectionDecorator.getClass().getMethods()));
        methods.removeAll(Arrays.asList(Object.class.getMethods()));
        return methods;
    }

    @Override
    protected ConnectionDecorator getConnectionDecorator(Connection target, ConnectionCallback callback) {
        return new Java7ConnectionDecorator(target, callback);
    }
}
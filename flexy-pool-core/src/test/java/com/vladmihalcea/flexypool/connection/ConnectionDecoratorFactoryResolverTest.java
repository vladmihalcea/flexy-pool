package com.vladmihalcea.flexypool.connection;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <code>ConnectionDecoratorFactoryResolverTest</code> - ConnectionDecoratorFactoryResolver Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionDecoratorFactoryResolverTest {

    @Test
    public void testResolve() {
        ConnectionDecoratorFactory factory = ConnectionDecoratorFactoryResolver.INSTANCE.resolve();
        assertEquals(getConnectionDecoratorFactoryClass(), factory.getClass());
    }

    protected Class<? extends ConnectionDecoratorFactory> getConnectionDecoratorFactoryClass() {
        return ConnectionDecoratorFactory.class;
    }
}
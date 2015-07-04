package com.vladmihalcea.flexypool.connection;

/**
 * <code>Java7ConnectionDecoratorFactoryResolverTest</code> - Java 1.7 ConnectionDecoratorFactoryResolverTest
 *
 * @author Vlad Mihalcea
 */
public class Java7ConnectionDecoratorFactoryResolverTest extends ConnectionDecoratorFactoryResolverTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends ConnectionDecoratorFactory> getConnectionDecoratorFactoryClass() {
        return Java7ConnectionDecoratorFactory.class;
    }
}

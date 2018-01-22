package com.vladmihalcea.flexypool.connection;

public class Java9ConnectionDecoratorFactoryResolverTest extends ConnectionDecoratorFactoryResolverTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends ConnectionDecoratorFactory> getConnectionDecoratorFactoryClass() {
        Runtime.class.getPackage().getImplementationVersion();
        return Java9ConnectionDecoratorFactory.class;
    }
}

package com.vladmihalcea.flexypool.connection;

/**
 * <code>CurrentJavaConnectionDecoratorFactoryResolverTest</code> - ConnectionDecoratorFactoryResolverTest that checks the current Java runtime version
 *
 * @author Vlad Mihalcea
 */
public class CurrentJavaConnectionDecoratorFactoryResolverTest extends ConnectionDecoratorFactoryResolverTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends ConnectionDecoratorFactory> getConnectionDecoratorFactoryClass() {
        Double version = Double.parseDouble(System.getProperty("java.specification.version"));
        return (version > 1.6d) ? Java7ConnectionDecoratorFactory.class : ConnectionDecoratorFactory.class;
    }
}

package com.vladmihalcea.flexypool.connection;

/**
 * ConnectionDecoratorProxyFactoryTest - ConnectionDecoratorProxyFactory Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionDecoratorProxyFactoryTest extends  AbstractConnectionProxyFactoryTest {

    @Override
    protected ConnectionProxyFactory newConnectionProxyFactory() {
        return new ConnectionDecoratorFactory();
    }
}
package com.vladmihalcea.flexypool.connection;

/**
 * JdkConnectionProxyFactoryTest - JdkConnectionProxyFactory Test
 *
 * @author Vlad Mihalcea
 */
public class JdkConnectionProxyFactoryTest extends  AbstractConnectionProxyFactoryTest {

    @Override
    protected ConnectionProxyFactory newConnectionProxyFactory() {
        return new JdkConnectionProxyFactory();
    }

}

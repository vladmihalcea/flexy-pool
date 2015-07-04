package com.vladmihalcea.flexypool.connection;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

/**
 * AbstractConnectionProxyFactoryTest - Base ConnectionProxyFactory Test
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractConnectionProxyFactoryTest {

    @Test
    public void testNewInstance() throws SQLException {
        Connection target = Mockito.mock(Connection.class);
        ConnectionPoolCallback callback = Mockito.mock(ConnectionPoolCallback.class);
        Connection proxy = newConnectionProxyFactory().newInstance(target, callback);
        verify(callback, times(1)).acquireConnection();
        verify(callback, never()).releaseConnection(anyLong());
        proxy.clearWarnings();
        proxy.close();
        verify(target, times(1)).clearWarnings();
        verify(callback, times(1)).releaseConnection(anyLong());
        verifyNoMoreInteractions(callback);
    }

    /**
     * Create a new ConnectionProxyFactory
     * @return new ConnectionProxyFactory to test
     */
    protected abstract ConnectionProxyFactory newConnectionProxyFactory();

}

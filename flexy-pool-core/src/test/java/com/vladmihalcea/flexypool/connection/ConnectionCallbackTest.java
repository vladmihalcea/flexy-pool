package com.vladmihalcea.flexypool.connection;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * ConnectionCallbackTest - ConnectionCallback Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionCallbackTest {

    @Test
    public void testAcquire() {
        ConnectionPoolCallback connectionPoolCallback = Mockito.mock(ConnectionPoolCallback.class);
        new ConnectionCallback(connectionPoolCallback);
        verify(connectionPoolCallback, times(1)).acquireConnection();
    }

    @Test
    public void testRelease() {
        ConnectionPoolCallback connectionPoolCallback = Mockito.mock(ConnectionPoolCallback.class);
        ConnectionCallback callback = new ConnectionCallback(connectionPoolCallback);
        verify(connectionPoolCallback, times(1)).acquireConnection();
        callback.close();
        verify(connectionPoolCallback, times(1)).releaseConnection(anyLong());
    }
}

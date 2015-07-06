package com.vladmihalcea.flexypool.event;

/**
 * <code>ConnectionAcquireTimeThresholdExceededEvent</code> - Event generated when a connection acquire has exceeded the given time threshold
 *
 * @author Vlad Mihalcea
 */
public class ConnectionAcquireTimeThresholdExceededEvent extends TimeThresholdExceededEvent {

    private static final long serialVersionUID = -2107982228572130887L;

    /**
     * {@inheritDoc}
     */
    public ConnectionAcquireTimeThresholdExceededEvent(String uniqueName,
                                                       long timeThresholdMillis, long actualTimeMillis) {
        super(uniqueName, timeThresholdMillis, actualTimeMillis);
    }
}

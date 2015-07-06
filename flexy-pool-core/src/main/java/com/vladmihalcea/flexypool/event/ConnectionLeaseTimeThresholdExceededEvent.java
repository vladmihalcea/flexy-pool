package com.vladmihalcea.flexypool.event;

/**
 * <code>ConnectionLeaseTimeThresholdExceededEvent</code> - Event generated when a connection lease has exceeded the given time threshold
 *
 * @author Vlad Mihalcea
 */
public class ConnectionLeaseTimeThresholdExceededEvent extends TimeThresholdExceededEvent {

    private static final long serialVersionUID = -2107982228572130887L;

    /**
     * {@inheritDoc}
     */
    public ConnectionLeaseTimeThresholdExceededEvent(String uniqueName,
                                                     long timeThresholdMillis, long actualTimeMillis) {
        super(uniqueName, timeThresholdMillis, actualTimeMillis);
    }
}

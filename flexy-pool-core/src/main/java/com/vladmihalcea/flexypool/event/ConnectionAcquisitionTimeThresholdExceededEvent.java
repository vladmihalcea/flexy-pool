package com.vladmihalcea.flexypool.event;

/**
 * <code>ConnectionAcquisitionTimeThresholdExceededEvent</code> - Event generated when a connection acquisition has exceeded the given time threshold
 *
 * @author Vlad Mihalcea
 */
public class ConnectionAcquisitionTimeThresholdExceededEvent extends TimeThresholdExceededEvent {

    private static final long serialVersionUID = -2107982228572130887L;

    /**
     * {@inheritDoc}
     */
    public ConnectionAcquisitionTimeThresholdExceededEvent(String uniqueName,
                                                           long timeThresholdMillis, long actualTimeMillis) {
        super(uniqueName, timeThresholdMillis, actualTimeMillis);
    }
}

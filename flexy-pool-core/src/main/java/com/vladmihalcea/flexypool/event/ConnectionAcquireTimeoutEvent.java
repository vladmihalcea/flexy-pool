package com.vladmihalcea.flexypool.event;

/**
 * <code>ConnectionAcquireTimeoutEvent</code> - Event generated when a connection acquire request has timed out
 *
 * @author Vlad Mihalcea
 */
public class ConnectionAcquireTimeoutEvent extends Event {

    private static final long serialVersionUID = -1769599416259900943L;

    /**
     * {@inheritDoc}
     */
    public ConnectionAcquireTimeoutEvent(String uniqueName) {
        super(uniqueName);
    }
}

package com.vladmihalcea.flexypool.event;

/**
 * <code>ConnectionAcquisitionTimeoutEvent</code> - Event generated when a connection acquisition request has timed out
 *
 * @author Vlad Mihalcea
 */
public class ConnectionAcquisitionTimeoutEvent extends Event {

    private static final long serialVersionUID = -1769599416259900943L;

    /**
     * {@inheritDoc}
     */
    public ConnectionAcquisitionTimeoutEvent(String uniqueName) {
        super(uniqueName);
    }
}

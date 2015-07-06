package com.vladmihalcea.flexypool.event;

import java.io.Serializable;

/**
 * <code>Event</code> - Base abstraction for all FlexyPool events
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public abstract class Event implements Serializable {

    private static final long serialVersionUID = 279420714392857536L;

    private final String uniqueName;

    /**
     * Init constructor
     * @param uniqueName FlexyPool unique name
     */
    protected Event(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * Get FlexyPool unique name this event has originated from
     * @return FlexyPool unique name
     */
    public String getUniqueName() {
        return uniqueName;
    }
}

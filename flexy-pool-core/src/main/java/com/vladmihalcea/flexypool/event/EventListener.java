package com.vladmihalcea.flexypool.event;

/**
 * EventListener - Event Handler
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 * @param <E> Event type
 */
public abstract class EventListener<E extends Event> {

    private final Class<E> eventClass;

    /**
     * Init constructor
     *
     * @param eventClass The listening event type
     */
    protected EventListener(Class<E> eventClass) {
        this.eventClass = eventClass;
    }

    /**
     * The event type this handler listens to.
     *
     * @return event class
     */
    public Class<E> listensTo() {
        return eventClass;
    }

    /**
     * Event handling logic
     *
     * @param event event
     */
    public abstract void on(E event);
}

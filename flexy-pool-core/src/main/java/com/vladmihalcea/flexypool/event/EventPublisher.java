package com.vladmihalcea.flexypool.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>EventPublisher</code> - This class associates events to their listeners
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public class EventPublisher {

    /**
     * Create a new {@link EventPublisher} instance using the current configured listeners
     *
     * @param eventListenerResolver event listener resolver
     * @return {@link EventPublisher}
     */
    public static EventPublisher newInstance(EventListenerResolver eventListenerResolver) {
        List<? extends EventListener<? extends Event>> eventListeners = eventListenerResolver != null ?
                eventListenerResolver.resolveListeners() : null;
        return eventListeners != null && !eventListeners.isEmpty() ?
                new EventPublisher(eventListeners) :
                new EventPublisher();
    }

    private Map<Class<? extends Event>, EventListener<? extends Event>> eventListenerMap =
            new HashMap<Class<? extends Event>, EventListener<? extends Event>>();

    /**
     * Init constructor
     *
     * @param eventListeners event listeners
     */
    public EventPublisher(List<? extends EventListener<? extends Event>> eventListeners) {
        for (EventListener<? extends Event> eventListener : eventListeners) {
            Class<? extends Event> eventClass = eventListener.listensTo();
            eventListenerMap.put(eventClass, eventListener);

        }
    }

    /**
     * Init constructor
     */
    protected EventPublisher() {
        this(new ArrayList<EventListener<? extends Event>>(0));
    }

    /**
     * Publish an {@link Event} to all listeners
     *
     * @param event event
     * @param <E> Event generic type
     */
    public <E extends Event> void publish(E event) {
        if (!eventListenerMap.isEmpty()) {
            EventListener<E> eventListener = getEventListener(event);
            if (eventListener != null) {
                eventListener.on(event);
            }
        }
    }

    /**
     * Get associated {@link EventListener}
     *
     * @param event event
     * @param <E>   event type
     * @return associated {@link EventListener}
     */
    private <E extends Event> EventListener<E> getEventListener(E event) {
        @SuppressWarnings("unchecked")
        EventListener<E> eventListener = (EventListener<E>) eventListenerMap.get(event.getClass());
        return eventListener;
    }
}

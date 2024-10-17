package com.vladmihalcea.flexypool.event;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

/**
 * <code>EventPublisherTest</code> - EventPublisher Test
 *
 * @author Vlad Mihalcea
 */
public class EventPublisherTest {

    public static class FirstEvent extends Event {

        protected FirstEvent(String uniqueName) {
            super(uniqueName);
        }
    }

    public static class SecondEvent extends Event {

        protected SecondEvent(String uniqueName) {
            super(uniqueName);
        }
    }

    public static class FirstEventListener extends EventListener<FirstEvent> {

        private FirstEvent event;

        protected FirstEventListener() {
            super(FirstEvent.class);
        }

        @Override
        public void on(FirstEvent event) {
            this.event = event;
        }
    }

    public static class SecondEventListener extends EventListener<SecondEvent> {

        private SecondEvent event;

        protected SecondEventListener() {
            super(SecondEvent.class);
        }

        @Override
        public void on(SecondEvent event) {
            this.event = event;
        }
    }

    @Test
    public void testPublishWithNoListeners() {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.publish(new ConnectionAcquisitionTimeoutEvent( "123"));
    }

    @Test
    public void testPublishWithListeners() {
        FirstEventListener firstEventListener = new FirstEventListener();
        SecondEventListener secondEventListener = new SecondEventListener();
        EventPublisher eventPublisher = new EventPublisher(
            Arrays.asList(firstEventListener, secondEventListener)
        );
        FirstEvent firstEvent = new FirstEvent("first");
        SecondEvent secondEvent = new SecondEvent("second");
        assertNull(firstEventListener.event);
        assertNull(secondEventListener.event);
        eventPublisher.publish(firstEvent);
        eventPublisher.publish(secondEvent);
        assertSame(firstEvent, firstEventListener.event);
        assertSame(secondEvent, secondEventListener.event);
    }

    @Test
    public void testNewInstance() {
        FirstEventListener firstEventListener = new FirstEventListener();
        FirstEvent firstEvent = new FirstEvent("first");
        EventListenerResolver eventListenerResolver = Mockito.mock(EventListenerResolver.class);
        List<FirstEventListener> eventListeners = Collections.<FirstEventListener>singletonList(firstEventListener);
        when(eventListenerResolver.resolveListeners()).thenReturn((List) eventListeners);
        EventPublisher eventPublisher = EventPublisher.newInstance(eventListenerResolver);
        assertNotNull(eventPublisher);
        assertNull(firstEventListener.event);
        eventPublisher.publish(firstEvent);
        assertSame(firstEvent, firstEventListener.event);
    }

}
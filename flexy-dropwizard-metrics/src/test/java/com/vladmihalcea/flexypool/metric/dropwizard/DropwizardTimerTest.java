package com.vladmihalcea.flexypool.metric.dropwizard;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * DropwizardTimerTest - DropwizardTimer Test
 *
 * @author Vlad Mihalcea
 */
public class DropwizardTimerTest {

    private io.dropwizard.metrics.Timer timer;

    private DropwizardTimer timerWrapper;

    @Before
    public void before() {
        timer = new io.dropwizard.metrics.Timer();
        timerWrapper = new DropwizardTimer(timer);
    }

    @Test
    public void testUpdate() {
        assertEquals(0, timer.getCount());
        timerWrapper.update(100, TimeUnit.NANOSECONDS);
        assertEquals(1, timer.getCount());
    }
}

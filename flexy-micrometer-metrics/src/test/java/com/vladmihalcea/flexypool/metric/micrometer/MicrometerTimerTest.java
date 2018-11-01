package com.vladmihalcea.flexypool.metric.micrometer;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * MicrometerTimerTest - MicrometerTimer Test
 *
 * @author Vlad Mihalcea
 */
public class MicrometerTimerTest {

    private io.micrometer.core.instrument.Timer  timer;

    private MicrometerTimer timerWrapper;

    @Before
    public void before() {
        timer = new io.micrometer.core.instrument.simple.SimpleMeterRegistry().timer("test");
        timerWrapper = new MicrometerTimer(timer);
    }

    @Test
    public void testUpdate() {
        assertEquals(0, timer.count());
        timerWrapper.update(100, TimeUnit.NANOSECONDS);
        assertEquals(1, timer.count());
    }
}

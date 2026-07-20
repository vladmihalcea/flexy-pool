package com.vladmihalcea.flexypool.metric.micrometer;

import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;


import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MicrometerTimerTest - MicrometerTimer Test
 *
 * @author Vlad Mihalcea
 */
public class MicrometerTimerTest {

    private io.micrometer.core.instrument.Timer  timer;

    private MicrometerTimer timerWrapper;

    @BeforeEach
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

package com.vladmihalcea.flexy.metric.codahale;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * CodahaleTimerTest - CodahaleTimer Test
 *
 * @author Vlad Mihalcea
 */
public class CodahaleTimerTest {

    private com.codahale.metrics.Timer timer;

    private CodahaleTimer timerWrapper;

    @Before
    public void before() {
        timer = new com.codahale.metrics.Timer();
        timerWrapper = new CodahaleTimer(timer);
    }

    @Test
    public void testUpdate() {
        assertEquals(0, timer.getCount());
        timerWrapper.update(100, TimeUnit.NANOSECONDS);
        assertEquals(1, timer.getCount());
    }
}

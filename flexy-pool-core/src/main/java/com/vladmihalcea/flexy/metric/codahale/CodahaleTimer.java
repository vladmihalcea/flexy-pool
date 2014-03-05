package com.vladmihalcea.flexy.metric.codahale;

import com.vladmihalcea.flexy.metric.Timer;

import java.util.concurrent.TimeUnit;

/**
 * CodahaleTimer - CodahaleTimer
 *
 * @author Vlad Mihalcea
 */
public class CodahaleTimer implements Timer {

    private final com.codahale.metrics.Timer timer;

    public CodahaleTimer(com.codahale.metrics.Timer timer) {
        this.timer = timer;
    }

    @Override
    public void update(long duration, TimeUnit unit) {
        timer.update(duration, unit);
    }
}

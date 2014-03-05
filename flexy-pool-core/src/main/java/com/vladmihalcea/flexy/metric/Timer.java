package com.vladmihalcea.flexy.metric;

import java.util.concurrent.TimeUnit;

/**
 * Timer - Timer
 *
 * @author Vlad Mihalcea
 */
public interface Timer {

   void update(long duration, TimeUnit unit);
}

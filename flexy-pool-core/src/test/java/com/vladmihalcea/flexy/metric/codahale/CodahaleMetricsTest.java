package com.vladmihalcea.flexy.metric.codahale;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.Histogram;
import com.vladmihalcea.flexy.metric.Timer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * CodahaleMetricsTest - CodahaleMetrics Test
 *
 * @author Vlad Mihalcea
 */
public class CodahaleMetricsTest {

    private Configuration configuration;
    private CodahaleMetrics codahaleMetrics;

    @Before
    public void before() {
        configuration = new Configuration("domain");
        codahaleMetrics = new CodahaleMetrics(configuration, CodahaleMetricsTest.class);
    }

    @Test
    public void testHistogram() throws Exception {
        Histogram histogram = codahaleMetrics.histogram("histo");
        assertNotNull(histogram);
    }

    @Test
    public void testTimer() throws Exception {
        Timer timer = codahaleMetrics.timer("timer");
        assertNotNull(timer);
    }

    @Test
    public void testStartStop() throws Exception {
        codahaleMetrics.histogram("histo");
        codahaleMetrics.timer("timer");
        codahaleMetrics.start();
        codahaleMetrics.stop();
    }
}

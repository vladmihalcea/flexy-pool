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

    @Before
    public void before() {
        configuration = new Configuration("domain");        
    }

    @Test
    public void testHistogram() {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(configuration, CodahaleMetricsTest.class);
        Histogram histogram = codahaleMetrics.histogram("histo");
        assertNotNull(histogram);
    }

    @Test
    public void testTimer() {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(configuration, CodahaleMetricsTest.class);
        Timer timer = codahaleMetrics.timer("timer");
        assertNotNull(timer);
    }

    @Test
    public void testStartStopUsingDefaultConfiguration() {
        testStartStop(configuration);
    }

    @Test
    public void testStartStopUsingNoJmx() {
        configuration.setJmxEnabled(false);
        testStartStop(configuration);
    }

    public void testStartStop(Configuration currentConfiguration) {
        CodahaleMetrics codahaleMetrics = new CodahaleMetrics(currentConfiguration, CodahaleMetricsTest.class);
        codahaleMetrics.histogram("histo");
        codahaleMetrics.timer("timer");
        codahaleMetrics.start();
        codahaleMetrics.stop();
    }
}

package com.vladmihalcea.flexypool.metric;

import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * MockMetricsFactoryService - Mock MetricsFactoryService
 *
 * @author Vlad Mihalcea
 */
public class MockMetricsFactoryService implements MetricsFactoryService {

    /**
     * Load the Mock MetricsFactory
     *
     * @return Mock MetricsFactory
     */
    @Override
    public MetricsFactory load() {
        MetricsFactory metricsFactory = Mockito.mock(MetricsFactory.class);
        Metrics metrics = Mockito.mock(Metrics.class);
        Histogram histogram = Mockito.mock(Histogram.class);
        Timer timer = Mockito.mock(Timer.class);
        when(metrics.histogram(anyString())).thenReturn(histogram);
        when(metrics.timer(anyString())).thenReturn(timer);
        when(metricsFactory.newInstance(any(ConfigurationProperties.class))).thenReturn(metrics);
        return metricsFactory;
    }
}

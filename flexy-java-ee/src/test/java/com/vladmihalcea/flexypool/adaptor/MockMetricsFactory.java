package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.metric.Histogram;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.Timer;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

/**
 * MockMetricsFactory - Mock MetricsFactory
 *
 * @author Vlad Mihalcea
 */
public class MockMetricsFactory implements MetricsFactory {

    private static final Metrics metrics = Mockito.mock(Metrics.class);

    private static final Timer overallConnectionAcquireTimer = Mockito.mock(Timer.class);

    private static final Histogram concurrentConnectionCountHistogram = Mockito.mock(Histogram.class);

    private static final Histogram concurrentConnectionRequestCountHistogram = Mockito.mock(Histogram.class);

    private static final Timer connectionLeaseMillisTimer = Mockito.mock(Timer.class);

    private static final Timer connectionAcquireMillisTimer = Mockito.mock(Timer.class);

    @Override
    public Metrics newInstance(ConfigurationProperties configurationProperties) {
        Mockito.reset(metrics);
        when(metrics.timer(FlexyPoolDataSource.OVERALL_CONNECTION_ACQUIRE_MILLIS)).thenReturn(overallConnectionAcquireTimer);
        when(metrics.histogram(FlexyPoolDataSource.CONCURRENT_CONNECTIONS_HISTOGRAM)).thenReturn(concurrentConnectionCountHistogram);
        when(metrics.histogram(FlexyPoolDataSource.CONCURRENT_CONNECTION_REQUESTS_HISTOGRAM)).thenReturn(concurrentConnectionRequestCountHistogram);
        when(metrics.timer(FlexyPoolDataSource.CONNECTION_LEASE_MILLIS)).thenReturn(connectionLeaseMillisTimer);
        when(metrics.timer(AbstractPoolAdapter.CONNECTION_ACQUIRE_MILLIS)).thenReturn(connectionAcquireMillisTimer);
        return metrics;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public Timer getOverallConnectionAcquireTimer() {
        return overallConnectionAcquireTimer;
    }

    public Histogram getConcurrentConnectionCountHistogram() {
        return concurrentConnectionCountHistogram;
    }

    public Histogram getConcurrentConnectionRequestCountHistogram() {
        return concurrentConnectionRequestCountHistogram;
    }

    public Timer getConnectionLeaseMillisTimer() {
        return connectionLeaseMillisTimer;
    }
}

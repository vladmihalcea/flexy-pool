package com.vladmihalcea.flexypool.metric;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * MockMetricsFactoryService - Mock MetricsFactoryService
 *
 * @author Vlad Mihalcea
 */
public class MockMetricsFactoryService implements MetricsFactoryService {

    public static class MockMetrics extends AbstractMetrics {

        private Histogram histogram = Mockito.mock(Histogram.class);
        private Timer timer = Mockito.mock(Timer.class);

        /**
         * Create {@link AbstractMetrics} from the given {@link ConfigurationProperties}
         */
        protected MockMetrics() {
            super(null);
        }

        @Override
        public Histogram histogram(String name) {
            return histogram;
        }

        @Override
        public Timer timer(String name) {
            return timer;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }
    }

    /**
     * Load the Mock MetricsFactory
     *
     * @return Mock MetricsFactory
     */
    @Override
    public MetricsFactory load() {
        MetricsFactory metricsFactory = Mockito.mock(MetricsFactory.class);
        Metrics metrics = new MockMetrics();
        when(metricsFactory.newInstance(any(ConfigurationProperties.class))).thenReturn(metrics);
        return metricsFactory;
    }
}

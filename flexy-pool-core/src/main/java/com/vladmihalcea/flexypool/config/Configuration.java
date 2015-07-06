package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.connection.ConnectionDecoratorFactoryResolver;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.event.EventListenerResolver;
import com.vladmihalcea.flexypool.event.EventPublisher;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.MetricsFactoryResolver;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * <code>Configuration</code> defines all required external associations for a given FlexyPool instance.
 * An instance is retrieved through the {@link com.vladmihalcea.flexypool.config.Configuration.Builder} which takes care of the actual order of setting required dependencies.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public final class Configuration<T extends DataSource> extends ConfigurationProperties<T, Metrics, PoolAdapter<T>> {

    public static final long DEFAULT_METRIC_LOG_REPORTER_MILLIS = TimeUnit.MINUTES.toMillis(5);

    /**
     * A Factory for configuration data.
     *
     * @param <T> the data source type
     */
    public static class Builder<T extends DataSource> {
        private final String uniqueName;
        private final T targetDataSource;
        private final PoolAdapterFactory<T> poolAdapterFactory;
        private MetricsFactory metricsFactory = MetricsFactoryResolver.INSTANCE.resolve();
        private ConnectionProxyFactory connectionProxyFactory = ConnectionDecoratorFactoryResolver.INSTANCE.resolve();
        private boolean jmxEnabled = true;
        private boolean jmxAutoStart = false;
        private long metricLogReporterMillis = DEFAULT_METRIC_LOG_REPORTER_MILLIS;
        private EventListenerResolver eventListenerResolver;
        private long connectionAcquireTimeThresholdMillis = Long.MAX_VALUE;
        private long connectionLeaseTimeThresholdMillis = Long.MAX_VALUE;

        /**
         * Construct the builder with the mandatory associations.
         *
         * @param uniqueName         the configuration unique name (required if you have multiple flexypool pools running)
         * @param targetDataSource   target data source
         * @param poolAdapterFactory pool adaptor factory
         */
        public Builder(String uniqueName, T targetDataSource, PoolAdapterFactory<T> poolAdapterFactory) {
            this.uniqueName = uniqueName;
            this.targetDataSource = targetDataSource;
            this.poolAdapterFactory = poolAdapterFactory;
        }

        /**
         * Set metrics factory
         *
         * @param metricsFactory metrics factory
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setMetricsFactory(MetricsFactory metricsFactory) {
            this.metricsFactory = metricsFactory;
            return this;
        }

        /**
         * Set connection proxy factory.
         *
         * @param connectionProxyFactory connection proxy factory
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setConnectionProxyFactory(ConnectionProxyFactory connectionProxyFactory) {
            this.connectionProxyFactory = connectionProxyFactory;
            return this;
        }

        /**
         * Enable/Disable jmx
         *
         * @param enableJmx jmx enabling
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setJmxEnabled(boolean enableJmx) {
            this.jmxEnabled = enableJmx;
            return this;
        }

        /**
         * Enable/Disable jmx auto-start
         *
         * @param jmxAutoStart jmx auto-started
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setJmxAutoStart(boolean jmxAutoStart) {
            this.jmxAutoStart = jmxAutoStart;
            return this;
        }

        /**
         * Set metric log report millis
         *
         * @param metricLogReporterMillis millis between two consecutive log reports
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setMetricLogReporterMillis(long metricLogReporterMillis) {
            this.metricLogReporterMillis = metricLogReporterMillis;
            return this;
        }

        /**
         * Set the event listener resolver
         *
         * @param eventListenerResolver event listener resolver
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setEventListenerResolver(EventListenerResolver eventListenerResolver) {
            this.eventListenerResolver = eventListenerResolver;
            return this;
        }

        /**
         * Set the connection acquire time threshold millis
         *
         * @param connectionAcquireTimeThresholdMillis connection acquire time threshold millis
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setConnectionAcquireTimeThresholdMillis(Long connectionAcquireTimeThresholdMillis) {
            if (connectionAcquireTimeThresholdMillis != null) {
                this.connectionAcquireTimeThresholdMillis = connectionAcquireTimeThresholdMillis;
            }
            return this;
        }

        /**
         * Set the connection lease time threshold millis
         *
         * @param connectionLeaseTimeThresholdMillis connection lease time threshold millis
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setConnectionLeaseTimeThresholdMillis(Long connectionLeaseTimeThresholdMillis) {
            if (connectionLeaseTimeThresholdMillis != null) {
                this.connectionLeaseTimeThresholdMillis = connectionLeaseTimeThresholdMillis;
            }
            return this;
        }

        /**
         * Build the configuration object.
         *
         * @return configuration
         */
        public Configuration<T> build() {
            EventPublisher eventPublisher = EventPublisher.newInstance(eventListenerResolver);
            Configuration<T> configuration = new Configuration<T>(uniqueName, targetDataSource, eventPublisher);
            configuration.setJmxEnabled(jmxEnabled);
            configuration.setJmxAutoStart(jmxAutoStart);
            configuration.setMetricLogReporterMillis(metricLogReporterMillis);
            configuration.setConnectionAcquireTimeThresholdMillis(connectionAcquireTimeThresholdMillis);
            configuration.setConnectionLeaseTimeThresholdMillis(connectionLeaseTimeThresholdMillis);
            configuration.metrics = metricsFactory.newInstance(configuration);
            configuration.poolAdapter = poolAdapterFactory.newInstance(configuration);
            configuration.connectionProxyFactory = connectionProxyFactory;
            return configuration;
        }
    }

    private final T targetDataSource;
    private Metrics metrics;
    private PoolAdapter<T> poolAdapter;
    private ConnectionProxyFactory connectionProxyFactory;

    private Configuration(String uniqueName, T targetDataSource, EventPublisher eventPublisher) {
        super(uniqueName, eventPublisher);
        this.targetDataSource = targetDataSource;
    }

    /**
     * Get the target data source
     *
     * @return target data source
     */
    public T getTargetDataSource() {
        return targetDataSource;
    }

    /**
     * Get the associated {@link com.vladmihalcea.flexypool.metric.Metrics}
     *
     * @return {@link com.vladmihalcea.flexypool.metric.Metrics}
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Get the associated {@link com.vladmihalcea.flexypool.adaptor.PoolAdapter}
     *
     * @return {@link com.vladmihalcea.flexypool.adaptor.PoolAdapter}
     */
    public PoolAdapter<T> getPoolAdapter() {
        return poolAdapter;
    }

    /**
     * Get the associated {@link com.vladmihalcea.flexypool.connection.ConnectionProxyFactory}
     *
     * @return {@link com.vladmihalcea.flexypool.connection.ConnectionProxyFactory}
     */
    public ConnectionProxyFactory getConnectionProxyFactory() {
        return connectionProxyFactory;
    }
}

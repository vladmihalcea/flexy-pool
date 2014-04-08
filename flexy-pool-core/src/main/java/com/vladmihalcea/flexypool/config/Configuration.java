package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.connection.JdkConnectionProxyFactory;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsFactory;
import com.vladmihalcea.flexypool.metric.codahale.CodahaleMetrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;

import javax.sql.DataSource;

/**
 * <code>Configuration</code> defines all required external associations for a given Flexy Pool instance.
 * An instance is retrieved through the {@link com.vladmihalcea.flexypool.config.Configuration.Builder} which takes care
 * of the actual order of setting required dependencies.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public final class Configuration<T extends DataSource> extends ConfigurationProperties<T, Metrics, PoolAdapter<T>> {

    public static final long DEFAULT_METRIC_LOG_REPORTER_PERIOD = 5;

    /**
     * A Factory for configuration data.
     *
     * @param <T> the data source type
     */
    public static class Builder<T extends DataSource> {
        private final String uniqueName;
        private final T targetDataSource;
        private final PoolAdapterFactory<T> poolAdapterFactory;
        private MetricsFactory metricsFactory = CodahaleMetrics.INSTANCE;
        private ConnectionProxyFactory connectionProxyFactory = JdkConnectionProxyFactory.INSTANCE;
        private boolean jmxEnabled = true;
        private long metricLogReporterPeriod = DEFAULT_METRIC_LOG_REPORTER_PERIOD;

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
         * @param metricsFactory metrics factory
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setMetricsFactory(MetricsFactory metricsFactory) {
            this.metricsFactory = metricsFactory;
            return this;
        }

        /**
         * Set connection proxy factory.
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
         * Set metric log report period
         *
         * @param metricLogReporterPeriod the period between two consecutive log reports
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder<T> setMetricLogReporterPeriod(long metricLogReporterPeriod) {
            this.metricLogReporterPeriod = metricLogReporterPeriod;
            return this;
        }

        /**
         * Build the configuration object.
         *
         * @return configuration
         */
        public Configuration<T> build() {
            Configuration<T> configuration = new Configuration<T>(uniqueName, targetDataSource);
            configuration.setJmxEnabled(jmxEnabled);
            configuration.setMetricLogReporterPeriod(metricLogReporterPeriod);
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

    private Configuration(String uniqueName, T targetDataSource) {
        super(uniqueName);
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
     * @return {@link com.vladmihalcea.flexypool.connection.ConnectionProxyFactory}
     */
    public ConnectionProxyFactory getConnectionProxyFactory() {
        return connectionProxyFactory;
    }
}

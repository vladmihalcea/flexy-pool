package com.vladmihalcea.flexy.config;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.factory.MetricsFactory;
import com.vladmihalcea.flexy.factory.PoolAdapterFactory;
import com.vladmihalcea.flexy.metric.Metrics;

import javax.sql.DataSource;

/**
 * <code>Configuration</code> defines all required external associations for a given Flexy Pool instance.
 * An instance is retrieved through the {@link com.vladmihalcea.flexy.config.Configuration.Builder} which takes care
 * of the actual order of setting required dependencies.
 *
 * @author	Vlad Mihalcea
 * @version	%I%, %E%
 * @since	1.0
 */
public class Configuration<T extends DataSource> {

    public static final long DEFAULT_METRIC_LOG_REPORTER_PERIOD = 5;

    /**
     * A Builder for configuration data.
     * @param <T> the data source type
     */
    public static class Builder<T extends DataSource> {
        private final String uniqueName;
        private final T targetDataSource;
        private final PoolAdapterFactory<T> poolAdapterFactory;
        private final MetricsFactory metricsFactory;
        private boolean jmxEnabled = true;
        private long metricLogReporterPeriod = DEFAULT_METRIC_LOG_REPORTER_PERIOD;

        /**
         * Construct the builder with the mandatory associations.
         * @param uniqueName the configuration unique name (required if you have multiple flexy pools running)
         * @param targetDataSource target data source
         * @param metricsFactory metrics factory
         * @param poolAdapterFactory pool adaptor factory
         */
        public Builder(String uniqueName, T targetDataSource, MetricsFactory metricsFactory, PoolAdapterFactory<T> poolAdapterFactory) {
            this.uniqueName = uniqueName;
            this.targetDataSource = targetDataSource;
            this.metricsFactory = metricsFactory;
            this.poolAdapterFactory = poolAdapterFactory;
        }

        /**
         * Enable/Disable jmx
         * @param enableJmx jmx enabling
         * @return this {@link com.vladmihalcea.flexy.config.Configuration.Builder}
         */
        public Builder setJmxEnabled(boolean enableJmx) {
            this.jmxEnabled = enableJmx;
            return this;
        }

        /**
         * Set metric log report period
         * @param metricLogReporterPeriod the period between two consecutive log reports
         * @return this {@link com.vladmihalcea.flexy.config.Configuration.Builder}
         */
        public Builder setMetricLogReporterPeriod(long metricLogReporterPeriod) {
            this.metricLogReporterPeriod = metricLogReporterPeriod;
            return this;
        }

        /**
         * Build the configuration object.
         * @return configuration
         */
        public Configuration<T> build() {
            Configuration<T> configuration = new Configuration<T>(uniqueName, targetDataSource);
            configuration.jmxEnabled = jmxEnabled;
            configuration.metricLogReporterPeriod = metricLogReporterPeriod;
            configuration.metrics = metricsFactory.newInstance(configuration);
            configuration.poolAdapter = poolAdapterFactory.newInstance(configuration);
            return configuration;
        }
    }

    private final String uniqueName;
    private final T targetDataSource;
    private boolean jmxEnabled;
    private long metricLogReporterPeriod;
    private Metrics metrics;
    private PoolAdapter poolAdapter;

    private Configuration(String uniqueName, T targetDataSource) {
        this.uniqueName = uniqueName;
        this.targetDataSource = targetDataSource;
    }

    /**
     * Get the the configuration unique name (required if you have multiple flexy pools running)
     * @return unique name
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     *  Get the target data source
     * @return target data source
     */
    public T getTargetDataSource() {
        return targetDataSource;
    }

    /**
     * Jmx availability
     * @return jmx availability
     */
    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    /**
     * Get the metric log report period
     * @return the period between two consecutive log reports
     */
    public long getMetricLogReporterPeriod() {
        return metricLogReporterPeriod;
    }

    /**
     * Get the associated {@link com.vladmihalcea.flexy.metric.Metrics}
     * @return {@link com.vladmihalcea.flexy.metric.Metrics}
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Get the associated {@link com.vladmihalcea.flexy.adaptor.PoolAdapter}
     * @return {@link com.vladmihalcea.flexy.adaptor.PoolAdapter}
     */
    public PoolAdapter getPoolAdapter() {
        return poolAdapter;
    }
}

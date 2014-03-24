package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapter;
import com.vladmihalcea.flexypool.adaptor.PoolAdapterBuilder;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.metric.MetricsBuilder;
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
     * A Builder for configuration data.
     *
     * @param <T> the data source type
     */
    public static class Builder<T extends DataSource> {
        private final String uniqueName;
        private final T targetDataSource;
        private final PoolAdapterBuilder<T> poolAdapterBuilder;
        private final MetricsBuilder metricsBuilder;
        private boolean jmxEnabled = true;
        private long metricLogReporterPeriod = DEFAULT_METRIC_LOG_REPORTER_PERIOD;

        /**
         * Construct the builder with the mandatory associations.
         *
         * @param uniqueName         the configuration unique name (required if you have multiple flexypool pools running)
         * @param targetDataSource   target data source
         * @param metricsBuilder     metrics builder
         * @param poolAdapterBuilder pool adaptor builder
         */
        public Builder(String uniqueName, T targetDataSource, MetricsBuilder metricsBuilder, PoolAdapterBuilder<T> poolAdapterBuilder) {
            this.uniqueName = uniqueName;
            this.targetDataSource = targetDataSource;
            this.metricsBuilder = metricsBuilder;
            this.poolAdapterBuilder = poolAdapterBuilder;
        }

        /**
         * Enable/Disable jmx
         *
         * @param enableJmx jmx enabling
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder setJmxEnabled(boolean enableJmx) {
            this.jmxEnabled = enableJmx;
            return this;
        }

        /**
         * Set metric log report period
         *
         * @param metricLogReporterPeriod the period between two consecutive log reports
         * @return this {@link com.vladmihalcea.flexypool.config.Configuration.Builder}
         */
        public Builder setMetricLogReporterPeriod(long metricLogReporterPeriod) {
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
            configuration.metrics = metricsBuilder.build(configuration);
            configuration.poolAdapter = poolAdapterBuilder.build(configuration);
            return configuration;
        }
    }

    private final T targetDataSource;
    private Metrics metrics;
    private PoolAdapter poolAdapter;

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
}

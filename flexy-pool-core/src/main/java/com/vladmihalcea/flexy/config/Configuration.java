package com.vladmihalcea.flexy.config;

import com.vladmihalcea.flexy.adaptor.PoolAdapter;
import com.vladmihalcea.flexy.factory.MetricsFactory;
import com.vladmihalcea.flexy.factory.PoolAdapterFactory;
import com.vladmihalcea.flexy.metric.Metrics;

import javax.sql.DataSource;

/**
 * Configuration - FlexyPool Configuration
 *
 * @author Vlad Mihalcea
 */
public class Configuration<T extends DataSource> {

    public static final long DEFAULT_METRIC_LOG_REPORTER_PERIOD = 5;

    public static class Builder<T extends DataSource> {
        private final String uniqueName;
        private final T targetDataSource;
        private final PoolAdapterFactory<T> poolAdapterFactory;
        private final MetricsFactory metricsFactory;
        private boolean jmxEnabled = true;
        private long metricLogReporterPeriod = DEFAULT_METRIC_LOG_REPORTER_PERIOD;

        public Builder(String uniqueName, T targetDataSource, MetricsFactory metricsFactory, PoolAdapterFactory<T> poolAdapterFactory) {
            this.uniqueName = uniqueName;
            this.targetDataSource = targetDataSource;
            this.metricsFactory = metricsFactory;
            this.poolAdapterFactory = poolAdapterFactory;
        }

        public Builder setJmxEnabled(boolean jmxEnabled) {
            this.jmxEnabled = jmxEnabled;
            return this;
        }

        public Builder setMetricLogReporterPeriod(long metricLogReporterPeriod) {
            this.metricLogReporterPeriod = metricLogReporterPeriod;
            return this;
        }

        public Configuration<T> build() {
            Configuration<T> configuration = new Configuration<T>(uniqueName, targetDataSource);
            configuration.metrics = metricsFactory.newInstance(configuration);
            configuration.poolAdapter = poolAdapterFactory.newInstance(configuration);
            configuration.jmxEnabled = jmxEnabled;
            configuration.metricLogReporterPeriod = metricLogReporterPeriod;
            return configuration;
        }
    }

    private final String uniqueName;
    private final T targetDataSource;
    private PoolAdapter poolAdapter;
    private Metrics metrics;
    private boolean jmxEnabled;
    private long metricLogReporterPeriod;

    private Configuration(String uniqueName, T targetDataSource) {
        this.uniqueName = uniqueName;
        this.targetDataSource = targetDataSource;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public T getTargetDataSource() {
        return targetDataSource;
    }

    public PoolAdapter getPoolAdapter() {
        return poolAdapter;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    public long getMetricLogReporterPeriod() {
        return metricLogReporterPeriod;
    }
}

package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.connection.ConnectionProxyBuilder;

import javax.sql.DataSource;

/**
 * <code>Configuration</code> defines basic properties for a given Flexy Pool instance.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public abstract class ConfigurationProperties<T extends DataSource, M, P> {

    private final String uniqueName;
    private boolean jmxEnabled;
    private long metricLogReporterPeriod;

    public ConfigurationProperties(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * Get the the configuration unique name (required if you have multiple flexypool pools running)
     *
     * @return unique name
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Jmx availability
     *
     * @return jmx availability
     */
    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    /**
     * Set jmx availability.
     *
     * @param jmxEnabled jmx availability
     */
    protected void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    /**
     * Get the metric log report period
     *
     * @return the period between two consecutive log reports
     */
    public long getMetricLogReporterPeriod() {
        return metricLogReporterPeriod;
    }

    /**
     * Set metric log report period
     *
     * @param metricLogReporterPeriod the period between two consecutive log reports
     */
    protected void setMetricLogReporterPeriod(long metricLogReporterPeriod) {
        this.metricLogReporterPeriod = metricLogReporterPeriod;
    }

    /**
     * Get the target data source
     *
     * @return target data source
     */
    public abstract T getTargetDataSource();

    /**
     * Get the associated metrics
     *
     * @return metrics
     */
    public abstract M getMetrics();

    /**
     * Get the associated pool adapter
     *
     * @return pool adapter
     */
    public abstract P getPoolAdapter();

    /**
     * Get connection proxy builder.
     * @return connection proxy builder
     */
    public abstract ConnectionProxyBuilder getConnectionProxyBuilder();
}

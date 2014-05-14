package com.vladmihalcea.flexypool.util;

import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;

import javax.sql.DataSource;

/**
 * <code>Configuration</code> defines basic properties for a given FlexyPool instance.
 *
 * @author Vlad Mihalcea
 * @version    %I%, %E%
 * @since 1.0
 */
public abstract class ConfigurationProperties<T extends DataSource, M, P> {

    private final String uniqueName;
    private boolean jmxEnabled;
    private long metricLogReporterMillis;

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
     * Get the metric log report millis
     *
     * @return millis between two consecutive log reports
     */
    public long getMetricLogReporterMillis() {
        return metricLogReporterMillis;
    }

    /**
     * Set metric log report millis
     *
     * @param metricLogReporterMillis millis between two consecutive log reports
     */
    protected void setMetricLogReporterMillis(long metricLogReporterMillis) {
        this.metricLogReporterMillis = metricLogReporterMillis;
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
     * Get connection proxy factory.
     * @return connection proxy factory
     */
    public abstract ConnectionProxyFactory getConnectionProxyFactory();
}

package com.vladmihalcea.flexypool.common;

import com.vladmihalcea.flexypool.connection.ConnectionProxyFactory;
import com.vladmihalcea.flexypool.event.EventPublisher;
import com.vladmihalcea.flexypool.strategy.MetricNamingStrategy;

import javax.sql.DataSource;

/**
 * <code>ConfigurationProperties</code> defines common properties that are shared amongst all FlexyPool components.
 * This class is decoupled from the main Configuration object to avoid dependency cycles.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public abstract class ConfigurationProperties<T extends DataSource, M, P> {

    private final String uniqueName;

    private final EventPublisher eventPublisher;

    private boolean jmxEnabled;

    private boolean jmxAutoStart;

    private long metricLogReporterMillis;

    private MetricNamingStrategy metricNamingStrategy;

    private long connectionAcquireTimeThresholdMillis = Long.MAX_VALUE;

    private long connectionLeaseTimeThresholdMillis = Long.MAX_VALUE;

    public ConfigurationProperties(String uniqueName, EventPublisher eventPublisher) {
        this.uniqueName = uniqueName;
        this.eventPublisher = eventPublisher;
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
     * Get event publisher
     *
     * @return event publisher
     */
    public EventPublisher getEventPublisher() {
        return eventPublisher;
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
     * Jmx auto-start
     *
     * @return jmx auto-start
     */
    public boolean isJmxAutoStart() {
        return jmxAutoStart;
    }

    /**
     * Set jmx auto-start.
     *
     * @param jmxAutoStart jmx auto-start
     */
    public void setJmxAutoStart(boolean jmxAutoStart) {
        this.jmxAutoStart = jmxAutoStart;
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
     * get metric naming strategy
     *
     * @return metric naming strategy
     */
    public MetricNamingStrategy getMetricNamingStrategy() {
        return metricNamingStrategy;
    }

    /**
     * Set metric naming strategy
     *
     * @param metricNamingStrategy metric naming strategy
     */
    public void setMetricNamingStrategy(MetricNamingStrategy metricNamingStrategy) {
        this.metricNamingStrategy = metricNamingStrategy;
    }

    /**
     * Get the connection acquire time threshold millis
     *
     * @return connection acquire time threshold millis
     */
    public long getConnectionAcquireTimeThresholdMillis() {
        return connectionAcquireTimeThresholdMillis;
    }

    /**
     * Set the connection acquire time threshold millis
     *
     * @param connectionAcquireTimeThresholdMillis connection acquire time threshold millis
     */
    public void setConnectionAcquireTimeThresholdMillis(long connectionAcquireTimeThresholdMillis) {
        this.connectionAcquireTimeThresholdMillis = connectionAcquireTimeThresholdMillis;
    }

    /**
     * Get the connection lease time threshold millis
     *
     * @return connection lease time threshold millis
     */
    public long getConnectionLeaseTimeThresholdMillis() {
        return connectionLeaseTimeThresholdMillis;
    }

    /**
     * Set the connection lease time threshold millis
     *
     * @param connectionLeaseTimeThresholdMillis connection lease time threshold millis
     */
    public void setConnectionLeaseTimeThresholdMillis(long connectionLeaseTimeThresholdMillis) {
        this.connectionLeaseTimeThresholdMillis = connectionLeaseTimeThresholdMillis;
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
     *
     * @return connection proxy factory
     */
    public abstract ConnectionProxyFactory getConnectionProxyFactory();
}

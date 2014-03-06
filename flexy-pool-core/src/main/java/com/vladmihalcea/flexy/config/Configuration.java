package com.vladmihalcea.flexy.config;

/**
 * Configuration - FlexyPool Configuration
 *
 * @author Vlad Mihalcea
 */
public class Configuration {

    public static final long DEFAULT_METRIC_LOG_REPORTER_PERIOD = 5;

    private final String uniqueName;
    private boolean jmxEnabled = true;
    private long metricLogReporterPeriod = DEFAULT_METRIC_LOG_REPORTER_PERIOD;

    public Configuration(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public long getMetricLogReporterPeriod() {
        return metricLogReporterPeriod;
    }

    public void setMetricLogReporterPeriod(long metricLogReporterPeriod) {
        this.metricLogReporterPeriod = metricLogReporterPeriod;
    }
}

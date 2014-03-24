package com.vladmihalcea.flexy.util;

/**
 * <code>Configuration</code> defines basic properties for a given Flexy Pool instance.
 *
 * @author	Vlad Mihalcea
 * @version	%I%, %E%
 * @since	1.0
 */
public class ConfigurationProperties {

    private final String uniqueName;
    private boolean jmxEnabled;
    private long metricLogReporterPeriod;

    public ConfigurationProperties(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * Get the the configuration unique name (required if you have multiple flexy pools running)
     * @return unique name
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Jmx availability
     * @return jmx availability
     */
    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    /**
     * Set jmx availability.
     * @param jmxEnabled jmx availability
     */
    protected void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    /**
     * Get the metric log report period
     * @return the period between two consecutive log reports
     */
    public long getMetricLogReporterPeriod() {
        return metricLogReporterPeriod;
    }

    /**
     * Set metric log report period
     * @param metricLogReporterPeriod the period between two consecutive log reports
     */
    protected void setMetricLogReporterPeriod(long metricLogReporterPeriod) {
        this.metricLogReporterPeriod = metricLogReporterPeriod;
    }
}

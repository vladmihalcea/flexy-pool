package com.vladmihalcea.flexy.config;

/**
 * FlexyConfiguration - FlexyPool Configuration
 *
 * @author Vlad Mihalcea
 */
public class FlexyConfiguration {

    private final String uniqueName;
    private boolean jmxEnabled = true;

    public FlexyConfiguration(String uniqueName) {
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
}

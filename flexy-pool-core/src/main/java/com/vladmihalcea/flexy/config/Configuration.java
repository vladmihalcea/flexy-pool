package com.vladmihalcea.flexy.config;

/**
 * Configuration - FlexyPool Configuration
 *
 * @author Vlad Mihalcea
 */
public class Configuration {

    private final String uniqueName;
    private boolean jmxEnabled = true;

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
}

package com.vladmihalcea.flexypool.util;

/**
 * TestUtils - Test Utils
 *
 * @author Vlad Mihalcea
 */
public class TestUtils {

    public static final String PERFORMANCE_TESTING = "performanceTesting";

    public static boolean isPerformanceTesting() {
        return Boolean.TRUE.toString().equals(System.getProperty(PERFORMANCE_TESTING));
    }
}

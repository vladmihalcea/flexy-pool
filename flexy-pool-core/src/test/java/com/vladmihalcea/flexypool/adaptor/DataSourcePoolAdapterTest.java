package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.config.FlexyPoolConfiguration;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

/**
 * DataSourcePoolAdapterTest - DataSourcePoolAdapter Test
 *
 * @author Vlad Mihalcea
 */
public class DataSourcePoolAdapterTest extends AbstractPoolAdapterTest {

    @Override
    protected AbstractPoolAdapter<DataSource> newPoolAdapter(FlexyPoolConfiguration<DataSource> configuration) {
        return new DataSourcePoolAdapter(configuration);
    }

    @Test
    public void testGetMaxPoolSize() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class, () -> getPoolAdapter().getMaxPoolSize());
    }

    @Test
    public void testSetMaxPoolSize() {
        org.junit.jupiter.api.Assertions.assertThrows(UnsupportedOperationException.class, () -> getPoolAdapter().setMaxPoolSize(10));
    }

    protected boolean supportsTimeoutExceptionTranslation() {
        return false;
    }
}
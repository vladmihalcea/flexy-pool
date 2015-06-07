package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.config.Configuration;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * DataSourcePoolAdapterTest - DataSourcePoolAdapter Test
 *
 * @author Vlad Mihalcea
 */
public class DataSourcePoolAdapterTest extends AbstractPoolAdapterTest {

    @Override
    protected AbstractPoolAdapter<DataSource> newPoolAdapter(Configuration<DataSource> configuration) {
        return new DataSourcePoolAdapter(configuration);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMaxPoolSize() {
        getPoolAdapter().getMaxPoolSize();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetMaxPoolSize() {
        getPoolAdapter().setMaxPoolSize(10);
    }
}
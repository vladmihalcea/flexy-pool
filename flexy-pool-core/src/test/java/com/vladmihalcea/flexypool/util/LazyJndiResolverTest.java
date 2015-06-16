package com.vladmihalcea.flexypool.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.naming.NameNotFoundException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * LazyJndiResolverTest - LazyJndiResolver Test
 *
 * @author Vlad Mihalcea
 */
public class LazyJndiResolverTest {

    private JndiTestUtils jndiTestUtils;

    @Before
    public void init() {
        jndiTestUtils = new JndiTestUtils();
    }

    @Test
    public void testLazyLookupFailure() {
        DataSource dataSource = LazyJndiResolver.newInstance("jdbc/DS", DataSource.class);
        try {
            dataSource.getLogWriter();
        } catch (Exception e) {
            assertEquals(NameNotFoundException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testLazyLookup() {
        jndiTestUtils.namingContext().bind("jdbc/DS", Mockito.mock(DataSource.class));
        DataSource dataSource = LazyJndiResolver.newInstance("jdbc/DS", DataSource.class);
        try {
            PrintWriter printWriter = new PrintWriter(System.out);
            when(dataSource.getLogWriter()).thenReturn(printWriter);
            assertSame(printWriter, dataSource.getLogWriter());
        } catch (SQLException e) {
            fail("There should be no exception thrown!");
        }
    }
}
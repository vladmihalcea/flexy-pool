package com.vladmihalcea.flexypool.util;

import org.junit.Test;
import org.mockito.Mockito;

import javax.naming.NoInitialContextException;
import javax.sql.DataSource;

import java.io.PrintWriter;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * LazyJndiResolverTest - LazyJndiResolver Test
 *
 * @author Vlad Mihalcea
 */
public class LazyJndiResolverTest {

    @Test
    public void testLazyLookupFailure() {
        DataSource dataSource = LazyJndiResolver.newInstance("jdbc/DS", DataSource.class);
        try {
            dataSource.getLogWriter();
        } catch (Exception e) {
            assertEquals(NoInitialContextException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testLazyLookup() {
        JndiTestUtils jndiTestUtils = new JndiTestUtils();
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
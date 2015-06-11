package com.vladmihalcea.flexypool.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * JndiUtilsTest - JndiUtils Test
 *
 * @author Vlad Mihalcea
 */
public class JndiUtilsTest {

    private SimpleNamingContextBuilder namingContext;

    @Before
    public void init() throws NamingException {
        namingContext = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    }

    @After
    public void destroy() throws NamingException {
        namingContext.clear();
    }

    @Test
    public void testLookupUnboundObject() {
        try {
            JndiUtils.lookup("abc");
            fail("There shouldn't be any object bound with this name");
        } catch (IllegalArgumentException e) {
            assertEquals(NameNotFoundException.class, e.getCause().getClass());
            assertEquals("abc was not found in JNDI", e.getMessage());
        }
    }

    @Test
    public void testLookupSuccess() {
        namingContext.bind("abc", "def");
        assertEquals("def", JndiUtils.lookup("abc"));
    }
}
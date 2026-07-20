package com.vladmihalcea.flexypool.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JndiUtilsTest - JndiUtils Test
 *
 * @author Vlad Mihalcea
 */
public class JndiUtilsTest extends AbstractUtilsTest<JndiUtils> {

    private SimpleNamingContextBuilder namingContext;

    @BeforeEach
    public void init() throws NamingException {
        namingContext = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    }

    @org.junit.jupiter.api.AfterEach
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

    @Override
    protected Class<JndiUtils> getUtilsClass() {
        return JndiUtils.class;
    }
}
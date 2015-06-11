package com.vladmihalcea.flexypool.util;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.NamingException;

/**
 * JndiTestUtils - Jndi Test Utils
 *
 * @author Vlad Mihalcea
 */
public class JndiTestUtils {

    private SimpleNamingContextBuilder namingContext;

    public JndiTestUtils() {
        try {
            namingContext = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
            namingContext.clear();
        } catch (NamingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public SimpleNamingContextBuilder namingContext() {
        return namingContext;
    }
}

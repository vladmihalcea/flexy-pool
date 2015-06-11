package com.vladmihalcea.flexypool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
 * JndiUtils - JNDI Utilities
 *
 * @author Vlad Mihalcea
 */
public class JndiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JndiUtils.class);

    private JndiUtils() {
        throw new UnsupportedOperationException("JndiUtils is not instantiable!");
    }

    /**
     * Lookup object in JNDI
     * @param name object name
     * @param <T> object class parameter type
     * @return object
     */
    public static <T> T lookup(String name) {
        InitialContext initialContext = initialContext();
        Object located;
        try {
            @SuppressWarnings("unchecked")
            T object = (T) initialContext.lookup(name);
            if (object == null) {
                throw new NameNotFoundException(name + " was found but is null");
            }
            return object;
        } catch (NameNotFoundException e) {
            throw new IllegalArgumentException(name + " was not found in JNDI", e);
        } catch (NamingException e) {
            throw new IllegalArgumentException("JNDI lookup failed", e);
        } finally {
            closeContext(initialContext);
        }
    }

    /**
     * Create InitialContext
     * @return InitialContext
     */
    protected static InitialContext initialContext() {
        try {
            return new InitialContext();
        } catch (NamingException e) {
            throw new IllegalStateException("Can't create the InitialContext object");
        }
    }

    /**
     * Close InitialContext
     * @param initialContext initial context
     */
    protected static void closeContext(InitialContext initialContext) {
        if (initialContext != null) {
            try {
                initialContext.close();
            }
            catch (NamingException e) {
                LOGGER.debug("Can't close InitialContext", e);
            }
        }
    }
}

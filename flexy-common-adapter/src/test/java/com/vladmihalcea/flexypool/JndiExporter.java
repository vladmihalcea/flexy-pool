package com.vladmihalcea.flexypool;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.NamingException;

/**
 * JndiExporter - JndiExporter
 *
 * @author Vlad Mihalcea
 */
public class JndiExporter<T> {

    private final String jndiName;

    private final T jndiValue;

    private final SimpleNamingContextBuilder namingContextBuilder;

    public JndiExporter(String jndiName, T jndiValue) throws NamingException {
        this.jndiName = jndiName;
        this.jndiValue = jndiValue;
        this.namingContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    }

    @PostConstruct
    public void init() {
        namingContextBuilder.bind(jndiName, jndiValue);
    }

    @PreDestroy
    public void destroy() {
        namingContextBuilder.clear();
    }
}

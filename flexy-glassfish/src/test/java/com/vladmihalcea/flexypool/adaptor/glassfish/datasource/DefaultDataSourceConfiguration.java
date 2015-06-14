package com.vladmihalcea.flexypool.adaptor.glassfish.datasource;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;

/**
 * DataSourceConfiguration - Default DataSource configuration
 *
 * @author Vlad Mihalcea
 */

@DataSourceDefinition(
        name = "java:global/jdbc/default",
        className = "org.hsqldb.jdbc.JDBCDataSource",
        url = "jdbc:hsqldb:mem:test",
        initialPoolSize = 3,
        maxPoolSize = 5
)
@Stateless
public class DefaultDataSourceConfiguration {
}
package com.vladmihalcea.flexypool.adaptor.tomee.datasource;

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
        properties = {
                "JtaManaged=false"
        }
)
@Stateless
public class DefaultDataSourceConfiguration {
}
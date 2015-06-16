package com.vladmihalcea.flexypool.adaptor.tomee.datasource;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;

/**
 * FlexyPoolDataSourceConfiguration - FlexyPool DataSource configuration
 *
 * @author Vlad Mihalcea
 */
@DataSourceDefinition(
        name = "java:global/jdbc/flexypool",
        className = "com.vladmihalcea.flexypool.FlexyPoolDataSource",
        properties = {
                "JtaManaged=false"
        }
)
@Stateless
public class FlexyPoolDataSourceConfiguration {
}
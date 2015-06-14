package com.vladmihalcea.flexypool.adaptor.glassfish.datasource;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;

/**
 * FlexyPoolDataSourceConfiguration - FlexyPool DataSource configuration
 *
 * @author Vlad Mihalcea
 */

@DataSourceDefinition(
        name = "java:global/jdbc/flexypool",
        className = "com.vladmihalcea.flexypool.FlexyPoolDataSource")
@Stateless
public class FlexyPoolDataSourceConfiguration {
}
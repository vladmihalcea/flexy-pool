package com.vladmihalcea.flexypool.adaptor;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;

/**
 * DataSourceConfiguration -
 *
 * @author Vlad Mihalcea
 */

@DataSourceDefinition(name = "java:global/jdbc/flexypool", className = "com.vladmihalcea.flexypool.FlexyPoolDataSource")
@Stateless
public class DataSourceConfiguration {
}
package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * <code>ManagedConnection</code> wraps a given {@link Connection} and delegates all calls to it.
 * It's used as a Proxy but without the inherent latency penalties.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class ManagedConnection implements Connection {

    private final Connection targetConnection;

    public ManagedConnection(final Connection targetConnection) {
        this.targetConnection = targetConnection;
    }

    public Statement createStatement() throws SQLException {
        return targetConnection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return targetConnection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return targetConnection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return targetConnection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        targetConnection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return targetConnection.getAutoCommit();
    }

    public void commit() throws SQLException {
        targetConnection.commit();
    }

    public void rollback() throws SQLException {
        targetConnection.rollback();
    }

    public void close() throws SQLException {
        targetConnection.close();
    }

    public boolean isClosed() throws SQLException {
        return targetConnection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return targetConnection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        targetConnection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return targetConnection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        targetConnection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return targetConnection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        targetConnection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return targetConnection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return targetConnection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        targetConnection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return targetConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return targetConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return targetConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return targetConnection.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        targetConnection.setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        targetConnection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return targetConnection.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return targetConnection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return targetConnection.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        targetConnection.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        targetConnection.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return targetConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return targetConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return targetConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return targetConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return targetConnection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return targetConnection.prepareStatement(sql, columnNames);
    }

    public Clob createClob() throws SQLException {
        return targetConnection.createClob();
    }

    public Blob createBlob() throws SQLException {
        return targetConnection.createBlob();
    }

    public NClob createNClob() throws SQLException {
        return targetConnection.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return targetConnection.createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return targetConnection.isValid(timeout);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        targetConnection.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        targetConnection.setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return targetConnection.getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return targetConnection.getClientInfo();
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return targetConnection.createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return targetConnection.createStruct(typeName, attributes);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return targetConnection.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return targetConnection.isWrapperFor(iface);
    }

    public void setSchema(String schema) throws SQLException {
        ReflectionUtils.invoke(
                targetConnection,
                ReflectionUtils.getMethod(targetConnection, "setSchema", String.class),
                schema);
    }

    public String getSchema() throws SQLException {
        return ReflectionUtils.invoke(
                targetConnection,
                ReflectionUtils.getMethod(targetConnection, "getSchema"));
    }

    public void abort(Executor executor) throws SQLException {
        ReflectionUtils.invoke(
                targetConnection,
                ReflectionUtils.getMethod(targetConnection, "abort", Executor.class),
                executor);
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        ReflectionUtils.invoke(
                targetConnection,
                ReflectionUtils.getMethod(targetConnection, "setNetworkTimeout", Executor.class, int.class),
                executor, milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        return (Integer) ReflectionUtils.invoke(
                targetConnection,
                ReflectionUtils.getMethod(targetConnection, "getNetworkTimeout"));
    }
}

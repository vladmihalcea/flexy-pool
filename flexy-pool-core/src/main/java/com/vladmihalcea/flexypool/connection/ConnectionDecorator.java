package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * <code>ConnectionDecorator</code> wraps a given {@link Connection} and delegates all calls to it.
 * It's used as a Proxy but without the inherent latency penalties.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class ConnectionDecorator implements Connection {

    private final Connection target;

    private final ConnectionCallback callback;

    public ConnectionDecorator(Connection target, ConnectionCallback callback) {
        this.target = target;
        this.callback = callback;
    }

    public Connection getTarget() {
        return target;
    }

    public ConnectionCallback getCallback() {
        return callback;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return target.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return target.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return target.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return target.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        target.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return target.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        target.commit();
    }

    @Override
    public void rollback() throws SQLException {
        target.rollback();
    }

    @Override
    public void close() throws SQLException {
        callback.close();
        target.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return target.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return target.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        target.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return target.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        target.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return target.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        target.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return target.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return target.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        target.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return target.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return target.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return target.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return target.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        target.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        target.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return target.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return target.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return target.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        target.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        target.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return target.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return target.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return target.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return target.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return target.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return target.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return target.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return target.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return target.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return target.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return target.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        target.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        target.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return target.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return target.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return target.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return target.createStruct(typeName, attributes);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return target.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return target.isWrapperFor(iface);
    }

    public void setSchema(String schema) throws SQLException {
        ReflectionUtils.invoke(
                target,
                ReflectionUtils.getMethod(target, "setSchema", String.class),
                schema);
    }

    public String getSchema() throws SQLException {
        return ReflectionUtils.invoke(
                target,
                ReflectionUtils.getMethod(target, "getSchema"));
    }

    public void abort(Executor executor) throws SQLException {
        ReflectionUtils.invoke(
                target,
                ReflectionUtils.getMethod(target, "abort", Executor.class),
                executor);
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        ReflectionUtils.invoke(
                target,
                ReflectionUtils.getMethod(target, "setNetworkTimeout", Executor.class, int.class),
                executor, milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        return (Integer) ReflectionUtils.invoke(
                target,
                ReflectionUtils.getMethod(target, "getNetworkTimeout"));
    }
}

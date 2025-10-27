package com.vladmihalcea.flexypool.connection;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * <code>ConnectionDecorator</code> wraps a given {@link Connection} and delegates all calls to it.
 * It's used as a Proxy but without the inherent latency penalties. The newer methods are invoked using Reflection so that we can
 * accommodate multiple JDK versions.
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement() throws SQLException {
        return target.createStatement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return target.prepareStatement(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return target.prepareCall(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String nativeSQL(String sql) throws SQLException {
        return target.nativeSQL(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        target.setAutoCommit(autoCommit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        return target.getAutoCommit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SQLException {
        target.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback() throws SQLException {
        target.rollback();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SQLException {
        callback.close();
        target.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() throws SQLException {
        return target.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return target.getMetaData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        target.setReadOnly(readOnly);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return target.isReadOnly();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        target.setCatalog(catalog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCatalog() throws SQLException {
        return target.getCatalog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        target.setTransactionIsolation(level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return target.getTransactionIsolation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return target.getWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWarnings() throws SQLException {
        target.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return target.createStatement(resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return target.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return target.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return target.getTypeMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        target.setTypeMap(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHoldability(int holdability) throws SQLException {
        target.setHoldability(holdability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHoldability() throws SQLException {
        return target.getHoldability();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return target.setSavepoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return target.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        target.rollback(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        target.releaseSavepoint(savepoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return target.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return target.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return target.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return target.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return target.prepareStatement(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return target.prepareStatement(sql, columnNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Clob createClob() throws SQLException {
        return target.createClob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Blob createBlob() throws SQLException {
        return target.createBlob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NClob createNClob() throws SQLException {
        return target.createNClob();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return target.createSQLXML();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        return target.isValid(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        target.setClientInfo(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        target.setClientInfo(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClientInfo(String name) throws SQLException {
        return target.getClientInfo(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        return target.getClientInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return target.createArrayOf(typeName, elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return target.createStruct(typeName, attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return target.unwrap(iface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return target.isWrapperFor(iface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSchema(String schema) throws SQLException {
        getTarget().setSchema(schema);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchema() throws SQLException {
        return getTarget().getSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abort(Executor executor) throws SQLException {
        getTarget().abort(executor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getTarget().setNetworkTimeout(executor, milliseconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return getTarget().getNetworkTimeout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginRequest() throws SQLException {
        getTarget().beginRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endRequest() throws SQLException {
        getTarget().endRequest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout)
            throws SQLException {
        return getTarget().setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
        return getTarget().setShardingKeyIfValid(shardingKey, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
        getTarget().setShardingKey(shardingKey, superShardingKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShardingKey(ShardingKey shardingKey) throws SQLException {
        getTarget().setShardingKey(shardingKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof ConnectionDecorator decorator) {
            return getTarget().equals(decorator.getTarget());
        }
        return getTarget().equals(obj);
    }

    @Override
    public int hashCode() {
        return getTarget().hashCode();
    }
}

package com.vladmihalcea.flexypool.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ShardingKey;
import java.util.concurrent.Executor;

/**
 * <code>Java9ManagedConnection</code> wraps a given {@link Connection} and delegates all calls to it.
 * It extends {@link ConnectionDecorator} and adds the Java 1.9 new methods.
 *
 * @author Vlad Mihalcea
 * @since 2.0.0
 */
public class Java9ConnectionDecorator extends ConnectionDecorator {

    public Java9ConnectionDecorator(Connection target, ConnectionCallback callback) {
        super(target, callback);
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
}

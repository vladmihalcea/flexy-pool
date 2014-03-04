package com.vladmihalcea.flexy.adaptor;

/**
 * PoolAdapter - Interface for retrieving the pool adapter
 *
 * @author Vlad Mihalcea
 */
public interface PoolAdapterAware {

    /**
     * Get pool adapter
     * @return pool adapter
     */
    PoolAdapter getPoolAdapter();
}

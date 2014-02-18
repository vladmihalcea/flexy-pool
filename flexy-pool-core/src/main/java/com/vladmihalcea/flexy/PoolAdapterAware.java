package com.vladmihalcea.flexy;

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

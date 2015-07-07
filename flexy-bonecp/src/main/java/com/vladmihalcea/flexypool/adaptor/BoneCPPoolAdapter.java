package com.vladmihalcea.flexypool.adaptor;

import com.jolbox.bonecp.BoneCPDataSource;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;

/**
 * <code>BoneCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the BoneCP {@link BoneCPDataSource}. BoneCp doesn't support pool resizing like other
 * connection pools, so we can't set the max pool size. We can get the pool size (which can shrink or
 * grow based on the BoneCP adaptive pool settings) and we can translate connection acquire timeout exceptions.
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class BoneCPPoolAdapter extends AbstractPoolAdapter<BoneCPDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Timed out waiting for a free available connection.";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<BoneCPDataSource> FACTORY = new PoolAdapterFactory<BoneCPDataSource>() {

        @Override
        public PoolAdapter<BoneCPDataSource> newInstance(
                ConfigurationProperties<BoneCPDataSource, Metrics, PoolAdapter<BoneCPDataSource>> configurationProperties) {
        return new BoneCPPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     */
    public BoneCPPoolAdapter(ConfigurationProperties<BoneCPDataSource, Metrics, PoolAdapter<BoneCPDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getTargetDataSource().getMaxConnectionsPerPartition();
    }

    /**
     * BoneCP does not support pool resizing natively, as C3P0.
     * This way, it's impossible to guarantee what will happen to the current acquired connections one the pool
     * has to be destroyed and recreated, only to take into consideration the new pool size.
     * Therefore, the safest approach is to throw an UnsupportedOperationException
     * whenever the max pool size is about to be changed and document the behavior.
     *
     * @param maxPoolSize the upper amount of pooled connections.
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        throw new UnsupportedOperationException("BoneCP doesn't reinitialize itself on pool size change");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isAcquireTimeoutException(Exception e) {
        return e.getMessage() != null && ACQUIRE_TIMEOUT_MESSAGE.equals(e.getMessage());
    }
}

package com.vladmihalcea.flexypool.adaptor;

import com.jolbox.bonecp.BoneCPDataSource;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.common.ConfigurationProperties;

import java.sql.SQLException;

/**
 * <code>BoneCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the BoneCP {@link BoneCPDataSource}
 *
 * @author Vlad Mihalcea
 * @since 1.0
 */
public class BoneCPPoolAdapter extends AbstractPoolAdapter<BoneCPDataSource> {

    public static final String ACQUIRE_TIMEOUT_MESSAGE = "Timed out waiting for a free available connection.";

    public static final PoolAdapterFactory<BoneCPDataSource> FACTORY = new PoolAdapterFactory<BoneCPDataSource>() {

        @Override
        public PoolAdapter<BoneCPDataSource> newInstance(
                ConfigurationProperties<BoneCPDataSource, Metrics, PoolAdapter<BoneCPDataSource>> configurationProperties) {
            return new BoneCPPoolAdapter(configurationProperties);
        }
    };

    public BoneCPPoolAdapter(ConfigurationProperties<BoneCPDataSource, Metrics, PoolAdapter<BoneCPDataSource>> configurationProperties) {
        super(configurationProperties);
    }

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
     * Translate the BonCP Exception to AcquireTimeoutException.
     *
     * @param e exception
     * @return translated exception
     */
    @Override
    protected SQLException translateException(Exception e) {
        if (e.getMessage() != null &&
                ACQUIRE_TIMEOUT_MESSAGE.equals(e.getMessage())) {
            return new AcquireTimeoutException(e);
        }
        return super.translateException(e);
    }
}

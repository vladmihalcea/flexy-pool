package com.vladmihalcea.flexypool.adaptor;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPDataSource;
import com.vladmihalcea.flexypool.exception.AcquireTimeoutException;
import com.vladmihalcea.flexypool.metric.Metrics;
import com.vladmihalcea.flexypool.util.ConfigurationProperties;
import com.vladmihalcea.flexypool.util.ReflectionUtils;

import java.sql.SQLException;

/**
 * <code>BoneCPPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the BoneCP {@link BoneCPDataSource}
 *
 * @author Vlad Mihalcea
 * @version %I%, %E%
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

    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        getTargetDataSource().setMaxConnectionsPerPartition(maxPoolSize);
        //BoneCP doesn't reinitialize itself on pool size change
        BoneCP boneCP = ReflectionUtils.invoke(getTargetDataSource(), ReflectionUtils.getMethod(getTargetDataSource(), "getPool"));
        boneCP.close();
        ReflectionUtils.setFieldValue(getTargetDataSource(), "pool", null);
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
        return new SQLException(e);
    }
}

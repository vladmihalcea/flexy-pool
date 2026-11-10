package com.vladmihalcea.flexypool.adaptor;

import com.vladmihalcea.flexypool.common.ConfigurationProperties;
import com.vladmihalcea.flexypool.metric.Metrics;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.AgroalConnectionPoolConfiguration;

import java.sql.SQLTimeoutException;
import java.util.Locale;

/**
 * <code>AgroalPoolAdapter</code> extends {@link AbstractPoolAdapter} and it adapts the required API to
 * communicate with the Agroal {@link AgroalDataSource}
 *
 * @author SnehaNprog
 * @since 3.1.2
 */
public class AgroalPoolAdapter extends AbstractPoolAdapter<AgroalDataSource> {

    /**
     * Agroal throws a plain {@link java.sql.SQLException} when the acquisition times out
     * (e.g. "Sorry, acquisition timeout!"), so the only way to recognize it is by message.
     */
    public static final String ACQUISITION_TIMEOUT_MESSAGE = "acquisition timeout";

    /**
     * Singleton factory object reference
     */
    public static final PoolAdapterFactory<AgroalDataSource> FACTORY = new PoolAdapterFactory<AgroalDataSource>() {

        @Override
        public PoolAdapter<AgroalDataSource> newInstance(
                ConfigurationProperties<AgroalDataSource, Metrics, PoolAdapter<AgroalDataSource>> configurationProperties) {
            return new AgroalPoolAdapter(configurationProperties);
        }
    };

    /**
     * Init constructor
     *
     * @param configurationProperties configuration properties
     */
    public AgroalPoolAdapter(ConfigurationProperties<AgroalDataSource, Metrics, PoolAdapter<AgroalDataSource>> configurationProperties) {
        super(configurationProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPoolSize() {
        return getPoolConfiguration().maxSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxPoolSize(int maxPoolSize) {
        AgroalConnectionPoolConfiguration poolConfiguration = getPoolConfiguration();
        poolConfiguration.setMaxSize(maxPoolSize);
        if (getConfigurationProperties().isMaintainFixedSizePool()) {
            poolConfiguration.setMinSize(maxPoolSize);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isTimeoutAcquisitionException(Exception e) {
        return e instanceof SQLTimeoutException ||
                (e.getMessage() != null && e.getMessage().toLowerCase(Locale.ROOT).contains(ACQUISITION_TIMEOUT_MESSAGE));
    }

    private AgroalConnectionPoolConfiguration getPoolConfiguration() {
        return getTargetDataSource().getConfiguration().connectionPoolConfiguration();
    }
}

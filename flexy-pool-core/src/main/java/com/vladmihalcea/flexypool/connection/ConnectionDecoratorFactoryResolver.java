package com.vladmihalcea.flexypool.connection;

import java.util.ServiceLoader;

/**
 * <code>ConnectionDecoratorFactoryResolver</code> - ConnectionDecoratorFactory Resolver
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public class ConnectionDecoratorFactoryResolver {

    public static final ConnectionDecoratorFactoryResolver INSTANCE = new ConnectionDecoratorFactoryResolver();

    private ServiceLoader<ConnectionDecoratorFactoryService> serviceLoader = ServiceLoader.load(ConnectionDecoratorFactoryService.class);

    private ConnectionDecoratorFactoryResolver() {
    }

    /**
     * Resolve ConnectionDecoratorFactory from the Service Provider Interface configuration
     * @return ConnectionDecoratorFactory
     */
    public ConnectionDecoratorFactory resolve() {
        int loadingIndex = Integer.MIN_VALUE;
        ConnectionDecoratorFactory connectionDecoratorFactory = null;
        for(ConnectionDecoratorFactoryService connectionDecoratorFactoryService : serviceLoader) {
            int currentLoadingIndex = connectionDecoratorFactoryService.loadingIndex();
            if(currentLoadingIndex > loadingIndex) {
                connectionDecoratorFactory = connectionDecoratorFactoryService.load();
                loadingIndex = currentLoadingIndex;
            }
        }
        if(connectionDecoratorFactory != null) {
            return connectionDecoratorFactory;
        }
        throw new IllegalStateException("No ConnectionDecoratorFactory could be loaded!");
    }
}

package com.vladmihalcea.flexypool.connection;

import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * <code>ConnectionDecoratorFactoryResolver</code> - Resolves the current available {@link ConnectionDecoratorFactory}.
 * The SPI will load each available {@link ConnectionDecoratorFactoryService} and based on the loading index the newer
 * loaders can override older ones.
 *
 * @author Vlad Mihalcea
 * @since 1.2.3
 */
public final class ConnectionDecoratorFactoryResolver {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ConnectionDecoratorFactoryResolver.class);

    public static final ConnectionDecoratorFactoryResolver INSTANCE = new ConnectionDecoratorFactoryResolver();

    private ServiceLoader<ConnectionDecoratorFactoryService> serviceLoader = ServiceLoader.load(ConnectionDecoratorFactoryService.class);

    private ConnectionDecoratorFactoryResolver() {
    }

    /**
     * Resolve ConnectionDecoratorFactory from the Service Provider Interface configuration
     *
     * @return ConnectionDecoratorFactory
     */
    public ConnectionDecoratorFactory resolve() {
        int loadingIndex = Integer.MIN_VALUE;
        ConnectionDecoratorFactory connectionDecoratorFactory = null;
        Iterator<ConnectionDecoratorFactoryService> connectionDecoratorFactoryServiceIterator = serviceLoader.iterator();
        while (connectionDecoratorFactoryServiceIterator.hasNext()) {
            try {
                ConnectionDecoratorFactoryService connectionDecoratorFactoryService = connectionDecoratorFactoryServiceIterator.next();
                int currentLoadingIndex = connectionDecoratorFactoryService.loadingIndex();
                if (currentLoadingIndex > loadingIndex) {
                    ConnectionDecoratorFactory currentConnectionDecoratorFactory = connectionDecoratorFactoryService.load();
                    if (currentConnectionDecoratorFactory != null) {
                        connectionDecoratorFactory = currentConnectionDecoratorFactory;
                        loadingIndex = currentLoadingIndex;
                    }
                }
            } catch (LinkageError e) {
                LOGGER.info("Couldn't load ConnectionDecoratorFactoryService on the current JVM", e);
            }
        }
        if (connectionDecoratorFactory != null) {
            return connectionDecoratorFactory;
        }
        throw new IllegalStateException("No ConnectionDecoratorFactory could be loaded!");
    }
}

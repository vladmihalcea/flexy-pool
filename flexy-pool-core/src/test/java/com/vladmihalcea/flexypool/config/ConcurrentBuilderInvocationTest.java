package com.vladmihalcea.flexypool.config;

import com.vladmihalcea.flexypool.adaptor.PoolAdapterFactory;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.fail;

public class ConcurrentBuilderInvocationTest {

    @Test
    public void testBuilder() throws InterruptedException, ExecutionException {
        DataSource dataSource = Mockito.mock(DataSource.class);
        PoolAdapterFactory<DataSource> poolAdapterFactory = Mockito.mock(PoolAdapterFactory.class);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<FlexyPoolConfiguration<DataSource>>> results = executorService.invokeAll(List.of(
            () -> createBuilder(dataSource, poolAdapterFactory),
            () -> createBuilder(dataSource, poolAdapterFactory)
        ));

        for (Future<FlexyPoolConfiguration<DataSource>> result : results) {
            try {
                result.get();
            } catch (NoSuchElementException e) {
                fail("NoSuchElementException  was thrown");
            } catch (IllegalStateException e) {
                fail("IllegalStateException(" + e.getMessage() + ")  was thrown");
            }
        }
    }

    public FlexyPoolConfiguration<DataSource> createBuilder(DataSource dataSource, PoolAdapterFactory<DataSource> poolAdapterFactory) {
        return new FlexyPoolConfiguration.Builder<>(
                UUID.randomUUID().toString(),
                dataSource,
                poolAdapterFactory
        ).build();
    }
}
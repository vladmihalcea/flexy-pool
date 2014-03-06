package com.vladmihalcea.flexy.context;

import com.vladmihalcea.flexy.config.Configuration;
import com.vladmihalcea.flexy.metric.Metrics;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ContextTest - Context Test
 *
 * @author Vlad Mihalcea
 */
public class ContextTest {

    @Mock
    private Metrics metrics;

    private Context context;

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        Configuration configuration = new Configuration(UUID.randomUUID().toString());
        this.context = new Context(configuration, metrics);
    }

    @Test
    public void testStart() throws Exception {
        context.start();
        verify(metrics, times(1)).start();
    }

    @Test
    public void testStop() throws Exception {
        context.stop();
        verify(metrics, times(1)).stop();
    }
}

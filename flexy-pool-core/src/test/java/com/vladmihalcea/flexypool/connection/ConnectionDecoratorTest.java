package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * ConnectionDecoratorTest - ConnectionDecorator Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionDecoratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionDecoratorTest.class);

    private static Map<Class<?>, Object> classToPrimitives = new HashMap<Class<?>, Object>() {{
        put(Boolean.TYPE, false);
        put(Byte.TYPE, 0);
        put(Short.TYPE, 0);
        put(Integer.TYPE, 0);
        put(Long.TYPE, 0L);
        put(Float.TYPE, 0F);
        put(Double.TYPE, 0D);
        put(Void.TYPE, null);
    }};

    private static Map<Class<?>, Object> classToFinalObjects = new HashMap<Class<?>, Object>() {{
        put(String.class, "");
        put(String[].class, new String[]{});
        put(int[].class, new int[]{});
        put(Object[].class, new Object[]{});
        put(Class.class, Object.class);
    }};

    private final Connection targetConnection = Mockito.mock(Connection.class);

    private final ConnectionCallback connectionCallback = Mockito.mock(ConnectionCallback.class);

    private ConnectionDecorator connectionDecorator = new ConnectionDecorator(targetConnection, connectionCallback);

    @Test
    public void testAllMethodsAreInvoked() {
        invokeAllMethods(connectionDecorator);
    }

    protected void invokeAllMethods(Connection connection) {
        for (Method method : Connection.class.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (!parameterType.isPrimitive()) {
                    Object finalObject = classToFinalObjects.get(parameterType);
                    parameters[i] = finalObject != null ? finalObject : Mockito.mock(parameterType);
                } else {
                    parameters[i] = classToPrimitives.get(parameterType);
                }
            }
            ReflectionUtils.invoke(connection, ReflectionUtils.getMethod(connection, method.getName(), parameterTypes), parameters);
        }
    }
}

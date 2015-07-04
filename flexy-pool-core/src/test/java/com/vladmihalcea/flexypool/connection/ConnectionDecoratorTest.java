package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;

/**
 * ConnectionDecoratorTest - ConnectionDecorator Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionDecoratorTest {

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

    @Test
    public void testAllMethodsAreInvoked() {
        Connection target = Mockito.mock(Connection.class);
        ConnectionCallback callback = Mockito.mock(ConnectionCallback.class);
        invokeAllMethods(getConnectionDecorator(target, callback));
    }

    protected void invokeAllMethods(ConnectionDecorator connectionDecorator) {
        for (Method method : getConnectionMethods(connectionDecorator)) {
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
            ReflectionUtils.invoke(connectionDecorator, ReflectionUtils.getMethod(connectionDecorator, method.getName(), parameterTypes), parameters);
        }
    }

    protected List<Method> getConnectionMethods(ConnectionDecorator connectionDecorator) {
        return Arrays.asList(Connection.class.getMethods());
    }

    protected ConnectionDecorator getConnectionDecorator(Connection target, ConnectionCallback callback) {
        return new ConnectionDecorator(target, callback);
    }
}

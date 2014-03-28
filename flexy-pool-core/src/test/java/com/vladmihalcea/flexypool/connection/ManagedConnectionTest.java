package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ManagedConnectionTest - ManagedConnection Test
 *
 * @author Vlad Mihalcea
 */
public class ManagedConnectionTest {

    private ManagedConnection managedConnection;

    private Map<Class<?>, Object> classToPrimitives = new HashMap<Class<?>, Object>() {{
        put(Boolean.TYPE, false);
        put(Byte.TYPE, 0);
        put(Short.TYPE, 0);
        put(Integer.TYPE, 0);
        put(Long.TYPE, 0L);
        put(Float.TYPE, 0F);
        put(Double.TYPE, 0D);
        put(Void.TYPE, null);
    }};

    private Map<Class<?>, Object> classToFinalObjects = new HashMap<Class<?>, Object>() {{
        put(String.class, "");
        put(String[].class, new String[]{});
        put(int[].class, new int[]{});
        put(Object[].class, new Object[]{});
        put(Class.class, Object.class);
    }};

    @Test
    public void testAllMethodsAreInvoked() {
        Connection targetConnection = Mockito.mock(Connection.class);
        managedConnection = new ManagedConnection(targetConnection);
        for(Method method : Connection.class.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            List<ReflectionUtils.TypedObject> typedObjects = new ArrayList<ReflectionUtils.TypedObject>();
            for(Class<?> parameterType : parameterTypes) {
                if (!parameterType.isPrimitive()) {
                    Object finalObject = classToFinalObjects.get(parameterType);
                    typedObjects.add(new ReflectionUtils.TypedObject(
                            parameterType, finalObject != null ? finalObject : Mockito.mock(parameterType)
                    ));
                } else {
                    typedObjects.add(new ReflectionUtils.TypedObject(
                            parameterType, classToPrimitives.get(parameterType)
                    ));
                }
            }
            ReflectionUtils.invoke(managedConnection, method.getName(), typedObjects.toArray(new ReflectionUtils.TypedObject[]{}));
        }
    }
}

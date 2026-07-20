package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * ConnectionDecoratorTest - ConnectionDecorator Test
 *
 * @author Vlad Mihalcea
 */
public class ConnectionDecoratorTest {

    private static final Map<Class<?>, Object> CLASS_TO_PRIMITIVES = new HashMap<Class<?>, Object>() {{
        put(Boolean.TYPE, false);
        put(Byte.TYPE, 0);
        put(Short.TYPE, 0);
        put(Integer.TYPE, 0);
        put(Long.TYPE, 0L);
        put(Float.TYPE, 0F);
        put(Double.TYPE, 0D);
        put(Void.TYPE, null);
    }};

    private static final Map<Class<?>, Object> CLASS_TO_FINAL_OBJECTS = new HashMap<Class<?>, Object>() {{
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
        invokeAllMethods(new ConnectionDecorator(target, callback));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testEqualsHashCode(Object left, Object right, boolean shouldBeEqual) {
        final Integer rightHashCode = ofNullable(right).map(Object::hashCode).orElse(null);

        if (shouldBeEqual) {
            assertEquals(left, right);
            assertEquals(Integer.valueOf(left.hashCode()), rightHashCode);
        } else {
            assertNotEquals(left, right);
            assertNotEquals(Integer.valueOf(left.hashCode()), rightHashCode);
        }
    }

    private void invokeAllMethods(ConnectionDecorator connectionDecorator) {
        for (Method method : getConnectionMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (!parameterType.isPrimitive()) {
                    Object finalObject = CLASS_TO_FINAL_OBJECTS.get(parameterType);
                    parameters[i] = finalObject != null ? finalObject : Mockito.mock(parameterType);
                } else {
                    parameters[i] = CLASS_TO_PRIMITIVES.get(parameterType);
                }
            }
            ReflectionUtils.invoke(connectionDecorator, ReflectionUtils.getMethod(connectionDecorator, method.getName(), parameterTypes), parameters);
        }
    }

    private List<Method> getConnectionMethods() {
        return Arrays.asList(Connection.class.getMethods());
    }

    private static Stream<Arguments> parameters() {
        Connection connection = Mockito.mock(Connection.class);
        Connection anotherConnection = Mockito.mock(Connection.class);
        ConnectionCallback callback = Mockito.mock(ConnectionCallback.class);

        return Stream.of(
            Arguments.of(new ConnectionDecorator(connection, callback), new ConnectionDecorator(connection, callback), true),
            Arguments.of(new ConnectionDecorator(connection, callback), new ConnectionDecorator(anotherConnection, callback), false),
            Arguments.of(new ConnectionDecorator(connection, callback), connection, true),
            Arguments.of(new ConnectionDecorator(connection, callback), anotherConnection, false),
            Arguments.of(new ConnectionDecorator(connection, callback), null, false),
            Arguments.of(new ConnectionDecorator(connection, callback), new Object(), false)
        );
    }
}

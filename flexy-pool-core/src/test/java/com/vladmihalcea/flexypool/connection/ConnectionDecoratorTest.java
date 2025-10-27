package com.vladmihalcea.flexypool.connection;

import com.vladmihalcea.flexypool.util.ReflectionUtils;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.*;

import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * ConnectionDecoratorTest - ConnectionDecorator Test
 *
 * @author Vlad Mihalcea
 */
@RunWith(Enclosed.class)
public class ConnectionDecoratorTest {

    public static class MethodTests {
        private static Map<Class<?>, Object> classToPrimitives = new HashMap<>() {{
            put(Boolean.TYPE, false);
            put(Byte.TYPE, 0);
            put(Short.TYPE, 0);
            put(Integer.TYPE, 0);
            put(Long.TYPE, 0L);
            put(Float.TYPE, 0F);
            put(Double.TYPE, 0D);
            put(Void.TYPE, null);
        }};

        private static Map<Class<?>, Object> classToFinalObjects = new HashMap<>() {{
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

    @RunWith(Parameterized.class)
    public static class EqualHashCodeTests {
        private static final Connection connection = Mockito.mock(Connection.class);
        private static final Connection anotherConnection = Mockito.mock(Connection.class);
        private static final ConnectionCallback callback = Mockito.mock(ConnectionCallback.class);

        @Parameterized.Parameters
        public static Collection<Object[]> parameters() {
            return Arrays.asList(new Object[][]{
                            {new ConnectionDecorator(connection, callback), new ConnectionDecorator(connection, callback), true},
                            {new ConnectionDecorator(connection, callback), new ConnectionDecorator(anotherConnection, callback), false},
                            {new ConnectionDecorator(connection, callback), connection, true},
                            {new ConnectionDecorator(connection, callback), anotherConnection, false},
                            {new ConnectionDecorator(connection, callback), null, false},
                            {new ConnectionDecorator(connection, callback), new Object(), false}
                    }
            );
        }
        private final Object left;
        private final Object right;
        private final boolean shouldBeEqual;

        public EqualHashCodeTests(Object left, Object right, boolean shouldBeEqual) {
            this.left = left;
            this.right = right;
            this.shouldBeEqual = shouldBeEqual;
        }

        @Test
        public void testEqualsHashCode() {
            final Integer rightHashCode = ofNullable(right).map(Object::hashCode).orElse(null);

            if (shouldBeEqual) {
                assertEquals(left, right);
                assertEquals(Integer.valueOf(left.hashCode()), rightHashCode);
            } else {
                assertNotEquals(left, right);
                assertNotEquals(Integer.valueOf(left.hashCode()), rightHashCode);
            }
        }
    }
}

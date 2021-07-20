package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;

/**
 * The object responsible for actually serializing java objects
 */
@SuppressWarnings("unchecked")
public class ObjectSerializer {
    private static final String NULL_VALUE = "null";

    private final HashMap<Class<?>, JsonSerializer<?>> serializers;

    public ObjectSerializer() {
        this.serializers = new HashMap<>();

        addDefaultSerializer();
    }

    public void addFilter(Class<?> clazz, Filter filter) {
        getSerializer(clazz).addFilter(filter);
    }

    public <T> void setSerializer(@NotNull Class<T> clazz, @NotNull JsonSerializer<T> serializer) {
        this.serializers.put(clazz, serializer);
    }

    private void addDefaultSerializer() {
        Class<?>[] numbers = {long.class, Long.class, int.class, Integer.class, short.class, Short.class, byte.class, Byte.class, double.class, Double.class, float.class, Float.class, boolean.class, Boolean.class};

        for(Class<?> number: numbers) {
            serializers.put(number, new ImplSerializer<>() {
                @Override
                public String getSerializedValue(ObjectSerializer serializer, Object v) {
                    return v.toString();
                }
            });
        }


        Class<?>[] needsQuotes = {char.class, Character.class, String.class};

        for(Class<?> quoted: needsQuotes) {
            serializers.put(quoted, new ImplSerializer<>() {
                @Override
                public String getSerializedValue(ObjectSerializer serializer, Object v) {
                    return '\"' + v.toString() + '\"';
                }
            });
        }
    }

    public void removeFilters(Class<?> clazz) {
        getSerializer(clazz).addFilter(Filter.ANY);
    }

    @NotNull
    public <T> String serializeValue(T value) {
        if(value == null)
            return NULL_VALUE;

        if(value instanceof Collection<?>) {
            boolean notFirst = false;
            StringBuilder builder = new StringBuilder();
            builder.append('[');

            for(Object o: (Collection<?>)value) {
                if(notFirst)
                    builder.append(',');

                builder.append(serializeValue(o));
                notFirst = true;
            }
            builder.append(']');

            return builder.toString();
        } else if(value.getClass().isArray()) {
            int len = Array.getLength(value);
            StringBuilder builder = new StringBuilder();

            builder.append('[');

            for(int x = 0; x < len; x++) {
                if(x!=0)
                    builder.append(',');

                Object o = Array.get(value, x);

                builder.append(serializeValue(o));
            }

            builder.append(']');

            return builder.toString();
        } else {
            return getSerializer((Class<T>)value.getClass()).getSerializedValue(this, value);
        }
    }

    @NotNull
    public <T> JsonSerializer<T> getSerializer(Class<T> clazz) {
        JsonSerializer<T> serializer = (JsonSerializer<T>) serializers.get(clazz);

        if(serializer == null) {
            serializer = new JsonDescription<>(clazz);

            serializers.put(clazz, serializer);
        }

        return serializer;
    }

    /**
     * For primitive values, wrappers, and the string class
     *
     * @param <T>
     */
    private static abstract class ImplSerializer<T> implements JsonSerializer<T> {

        @Override
        public abstract String getSerializedValue(ObjectSerializer serializer, T v);

        @Override
        public void addFilter(Filter filter) {
        }
    }
}

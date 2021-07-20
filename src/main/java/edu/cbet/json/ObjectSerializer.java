package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

/**
 * The object responsible for actually serializing java objects
 */
@SuppressWarnings("unchecked")
public class ObjectSerializer {
    private final static HashSet<Class<?>> PRIMITIVE_NO_QUOTES = new HashSet<>();
    private final static HashSet<Class<?>> PRIMITIVE_QUOTES = new HashSet<>();

    static {
        PRIMITIVE_NO_QUOTES.addAll(List.of(long.class, Long.class, int.class, Integer.class, short.class, Short.class, byte.class, Byte.class, double.class, Double.class, float.class, Float.class, boolean.class, Boolean.class));
        PRIMITIVE_QUOTES.addAll(List.of(char.class, Character.class, String.class));
    }

    private static final String NULL_VALUE = "null";

    private final HashMap<Class<?>, JsonSerializer<?>> serializers;
    private final HashMap<Class<?>, List<Consumer<HashMap<String, String>>>> modifiers;

    public ObjectSerializer() {
        this.serializers = new HashMap<>();
        this.modifiers = new HashMap<>();
    }

    public void addFilter(Class<?> clazz, Filter filter) {
        getSerializer(clazz).addFilter(filter);
    }

    public <T> void addModifier(Class<T> clazz, Consumer<HashMap<String, String>> consumer) {
        List<Consumer<HashMap<String, String>>> modList = modifiers.computeIfAbsent(clazz, k -> new LinkedList<>());

        modList.add(consumer);
    }

    public <T> void setSerializer(@NotNull Class<T> clazz, @NotNull JsonSerializer<T> serializer) {
        this.serializers.put(clazz, serializer);
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
        } else if(PRIMITIVE_NO_QUOTES.contains(value.getClass())) {
            return value.toString();
        } else if(PRIMITIVE_QUOTES.contains(value.getClass())) {
            return '\"' + value.toString() + '\"';
        } else {
            HashMap<String, String> map = getSerializer((Class<T>)value.getClass()).getSerializedValue(this, value);

            List<Consumer<HashMap<String, String>>> modList = modifiers.get(value.getClass());

            if(modList != null) {
                for(Consumer<HashMap<String, String>> modifier: modList) {
                    try { //A modifier could disrupt all serialization if the exceptions aren't caught
                        modifier.accept(map);
                    } catch (Exception ignore) {}
                }
            }

            return toJSONObjectString(map);
        }
    }

    private String toJSONObjectString(HashMap<String, String> map) {
        StringBuilder builder = new StringBuilder();
        boolean notFirst = false;

        builder.append('{');
        for(Map.Entry<String, String> entry: map.entrySet()) {
            if(notFirst)
                builder.append(',');

            builder.append(entry.getKey()).append(':').append(entry.getValue());
            notFirst = true;
        }

        builder.append('}');

        return builder.toString();
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

}

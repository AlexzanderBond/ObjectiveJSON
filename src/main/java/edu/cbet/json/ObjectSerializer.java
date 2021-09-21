package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The object responsible for actually serializing java objects
 */
@SuppressWarnings("unchecked")
public class ObjectSerializer {
    private final static HashSet<Class<?>> PRIMITIVE_NO_QUOTES = new HashSet<>();
    private final static HashSet<Class<?>> PRIMITIVE_QUOTES = new HashSet<>();

    static {
        PRIMITIVE_NO_QUOTES.addAll(List.of(long.class, Long.class, int.class, Integer.class, short.class, Short.class, byte.class, Byte.class, double.class, Double.class, float.class, Float.class, boolean.class, Boolean.class));
        PRIMITIVE_QUOTES.addAll(List.of(char.class, Character.class));
    }

    private static final String NULL_VALUE = "null";

    private final HashMap<Class<?>, JsonSerializer<?>> serializers;
    private final HashMap<Class<?>, List<Modifier<?>>> modifiers;
    private final HashMap<Class<?>, List<Filter>> filters;

    public ObjectSerializer() {
        this.serializers = new HashMap<>();
        this.modifiers = new HashMap<>();
        this.filters = new HashMap<>();
    }

    public <T> void addModifier(Class<T> clazz, Modifier<T> consumer) {
        List<Modifier<?>> modList = modifiers.computeIfAbsent(clazz, k -> new LinkedList<>());
        modList.add(consumer);
    }

    public <T> void setSerializer(@NotNull Class<T> clazz, @NotNull JsonSerializer<T> serializer) {
        this.serializers.put(clazz, serializer);
    }

    public void addFilter(Class<?> clazz, Filter filter) {
        List<Filter> fList = this.filters.computeIfAbsent(clazz, k -> new LinkedList<>());
        fList.add(filter);

        this.getSerializer(clazz).addFilter(filter);
    }

    public void removeFilters(Class<?> clazz) {
        List<Filter> fList = this.filters.get(clazz);

        if(fList != null)
            fList.clear();

        this.getSerializer(clazz).addFilter(Filter.ANY);
    }

    @NotNull
    public <T> String serializeValue(T value) {
        StringBuilder builder = new StringBuilder();
        return this.serializeValue0(builder, value).toString();
    }

    @NotNull
    public <T> StringBuilder serializeValue0(StringBuilder builder, T value) {
        if(value == null) {
            builder.append(NULL_VALUE);
        } else if(value instanceof CharSequence cs) {
            appendFixedString(builder, cs);
        } else if(value instanceof Collection<?>) {
            boolean notFirst = false;
            builder.append('[');

            for(Object o: (Collection<?>)value) {
                if(notFirst)
                    builder.append(',');

                this.serializeValue0(builder, o);
                notFirst = true;
            }
            builder.append(']');
        } else if(value instanceof Map<?, ?> map) {
            boolean notFirst = false;

            builder.append('{');
            for(Map.Entry<?, ?> entry: map.entrySet()) {
                if(notFirst)
                    builder.append(',');

                appendFixedString(builder, Objects.toString(entry.getKey()));
                builder.append(':');

                this.serializeValue0(builder, entry.getValue());

                notFirst = true;
            }
            builder.append('}');
        } else if(value.getClass().isArray()) {
            int len = Array.getLength(value);

            builder.append('[');

            for(int x = 0; x < len; x++) {
                if(x!=0)
                    builder.append(',');

                Object o = Array.get(value, x);

                this.serializeValue0(builder, o);
            }

            builder.append(']');
        } else if(PRIMITIVE_NO_QUOTES.contains(value.getClass())) {
            if(value instanceof Double dValue) {
                if(dValue.isNaN()) {
                    builder.append("\"NaN\"");
                } else if(dValue.isInfinite()) {
                    builder.append('\"').append(dValue).append( '\"');
                }
            } else if(value instanceof Float fValue) {
                if(fValue.isNaN()) {
                    builder.append("\"NaN\"");
                } else if(fValue.isInfinite()) {
                    builder.append('\"').append(fValue).append( '\"');
                }
            }
            builder.append(value);
        } else if(PRIMITIVE_QUOTES.contains(value.getClass())) {
            builder.append('\"').append(value).append('\"');
        } else if(value instanceof Enum<?> ev) {
            builder.append('\"').append(ev.name()).append('\"');
        } else if(value instanceof JsonValue v) {
            toJsonValueString(null, builder, v);
        } else {
            JsonValue obj = getSerializer((Class<T>)value.getClass()).getSerializedValue(this, value);

            List<Modifier<?>> modList = modifiers.get(value.getClass());

            if(modList != null) {
                for(Modifier<?> modifier: modList) {
                    try { //A modifier could disrupt all serialization if the exceptions aren't caught
                        ((Modifier<T>)modifier).modify(value, obj);
                    } catch (Exception ignore) {}
                }
            }

            this.toJsonValueString(value.getClass(), builder, obj);
        }

        return builder;
    }

    private void toJsonValueString(Class<?> clazz, StringBuilder builder, JsonValue value) {
        List<Filter> fList = this.filters.get(clazz);

        if(value.isObject()) {
            boolean notFirst = false;
            JsonObject obj = value.getAsObject();
            builder.append('{');
            if(fList != null) {
                for (Map.Entry<String, JsonValue> entry : obj.entrySet()) {
                    boolean notFiltered = true;

                    for(Filter filter: fList)
                        notFiltered &= filter.allowProperty(entry.getKey());

                    if(notFiltered) {
                        if (notFirst)
                            builder.append(',');

                        builder.append('\"').append(entry.getKey()).append('\"').append(':');
                        toJsonValueString(null, builder, entry.getValue());
                        notFirst = true;
                    }
                }
            } else {
                for (Map.Entry<String, JsonValue> entry : obj.entrySet()) {
                    if (notFirst)
                        builder.append(',');

                    builder.append('\"').append(entry.getKey()).append('\"').append(':');
                    toJsonValueString(null, builder, entry.getValue());
                    notFirst = true;
                }
            }

            builder.append('}');
        } else if(value.isArray()) {
            boolean notFirst = false;
            builder.append('[');

            for(JsonValue o: value.getAsArray()) {
                if(notFirst)
                    builder.append(',');

                toJsonValueString(null, builder, o);
                notFirst = true;
            }
            builder.append(']');
        } else if(value.isString()) {
            appendFixedString(builder, value.getAsString());
        } else if(value.isNumber()) {
            builder.append(value.getAsNumber());
        } else if(value.isBoolean()) {
            builder.append(value.getAsBoolean());
        } else if(value.isNull()) {
            builder.append("null");
        } else {
            builder.append(value);
        }
    }

    public <T> JsonValue fromObject(T obj) {
        return ((JsonSerializer<T>)getSerializer(obj.getClass())).getSerializedValue(this, obj);
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

    private static void appendFixedString(StringBuilder stringBuilder, CharSequence str) {
        stringBuilder.ensureCapacity(stringBuilder.length() + str.length()+2);

        stringBuilder.append('\"');

        for(int x = 0; x < str.length(); x++) {
            char c = str.charAt(x);
            if(c == '\"') {
                if(x == 0 || str.charAt(x-1) != '\\') {
                    stringBuilder.append("\\\"");
                } else {
                    stringBuilder.append('\"');
                }
            } else if(c == '\n') {
                stringBuilder.append("\\n");
            } else if (c == '\r') {
                stringBuilder.append("\\r");
            } else if(c == '\t') {
                stringBuilder.append("\\t");
            } else {
                stringBuilder.append(c);
            }
        }

        stringBuilder.append('\"');
    }
}

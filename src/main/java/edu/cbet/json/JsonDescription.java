package edu.cbet.json;

import edu.cbet.json.annotations.JsonAsString;
import edu.cbet.json.annotations.JsonIgnore;
import edu.cbet.json.annotations.JsonProperty;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Default serializer which breaks down Classes to be serialized
 */
public class JsonDescription<T> implements JsonSerializer<T> {
    private final List<FieldDescriptor<T>> fieldDescriptors;

    public JsonDescription(Class<T> clazz) {
        this.fieldDescriptors = new LinkedList<>();

        for(Field field: clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(JsonIgnore.class) && !field.isSynthetic()) {
                field.setAccessible(true);
                boolean alwaysString = field.isAnnotationPresent(JsonAsString.class);
                if(field.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty property = field.getAnnotation(JsonProperty.class);

                    if(property.value().isEmpty()) {
                        this.fieldDescriptors.add(new FieldDescriptor<>(field.getName(), field, alwaysString));
                    } else {
                        this.fieldDescriptors.add(new FieldDescriptor<>(property.value(), field, alwaysString));
                    }
                } else {
                    this.fieldDescriptors.add(new FieldDescriptor<>(field.getName(), field, alwaysString));
                }
            }
        }
    }

    @Override
    public JsonValue getSerializedValue(ObjectSerializer serializer, T v) {
        JsonObject map = new JsonObject();

        for(FieldDescriptor<T> descriptor: fieldDescriptors) {
            if(!descriptor.isActive())
                continue;

            Object value = descriptor.getValue(v);
            JsonValue jv = JsonNull.NULL;

            if(value instanceof JsonValue tjv)
                jv = tjv;
            else if(value instanceof String str)
                jv = JsonString.valueOf(str);
            else if(value instanceof Number n)
                jv = JsonNumber.valueOf(n);
            else if(value instanceof Boolean b)
                jv = JsonBoolean.valueOf(b);
            else if(value instanceof Character c)
                jv = JsonString.valueOf(c);
            else if(value instanceof Collection<?> c)
                jv = JsonArray.valueOf(c);
            else if(value instanceof Map<?, ?> m)
                jv = JsonObject.valueOf(m);
            else if(value != null) {
                jv = serializer.fromObject(value);
            }

            map.put(descriptor.getPropertyName(), jv);
        }

        return new JsonObject(map);
    }

    @Override
    public void addFilter(Filter filter) {
        for(FieldDescriptor<T> descriptor: fieldDescriptors) {
            descriptor.setActive(filter.allowProperty(descriptor.getPropertyName()));
        }
    }

    private static class FieldDescriptor<T> {
        private final String propertyName;
        private final Field field;
        private final boolean alwaysString;
        private boolean active;

        public FieldDescriptor(String propertyName, Field objField, boolean alwaysString) {
            this.propertyName = propertyName;
            this.alwaysString = alwaysString;
            this.field = objField;
            this.active = true;
        }

        public boolean isAlwaysString() {
            return alwaysString;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Object getValue(T v) {
            try {
                Object o = field.get(v);

                if(o != null && isAlwaysString()) {
                    return o.toString();
                } else {
                    return o;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

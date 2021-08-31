package edu.cbet.json;

import edu.cbet.json.annotations.JsonAsString;
import edu.cbet.json.annotations.JsonIgnore;
import edu.cbet.json.annotations.JsonProperty;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> getSerializedValue(ObjectSerializer serializer, T v) {
        HashMap<String, Object> map = new HashMap<>();

        for(FieldDescriptor<T> descriptor: fieldDescriptors) {
            if(!descriptor.isActive())
                continue;

            map.put(descriptor.getPropertyName(), descriptor.getValue(v));
        }

        return map;
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

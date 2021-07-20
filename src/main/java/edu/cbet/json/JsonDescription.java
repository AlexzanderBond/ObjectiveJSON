package edu.cbet.json;

import edu.cbet.json.annotations.JsonIgnore;
import edu.cbet.json.annotations.JsonProperty;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Default serializer which breaks down Classes to be serialized
 */
public class JsonDescription<T> implements JsonSerializer<T> {
    private final Class<T> clazz;
    private final List<FieldDescriptor<T>> fieldDescriptors;

    public JsonDescription(Class<T> clazz) {
        this.clazz = clazz;
        this.fieldDescriptors = new LinkedList<>();

        for(Field field: clazz.getDeclaredFields()) {
            if(!field.isAnnotationPresent(JsonIgnore.class) && !field.isSynthetic()) {
                field.setAccessible(true);
                if(field.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty property = field.getAnnotation(JsonProperty.class);

                    if(property.value().isEmpty()) {
                        this.fieldDescriptors.add(new FieldDescriptor<>(field.getName(), field));
                    } else {
                        this.fieldDescriptors.add(new FieldDescriptor<>(property.value(), field));
                    }
                } else {
                    this.fieldDescriptors.add(new FieldDescriptor<>(field.getName(), field));
                }
            }
        }
    }

    @Override
    public String getSerializedValue(ObjectSerializer serializer, T v) {
        StringBuilder builder = new StringBuilder();
        boolean notFirst = false;

        builder.append('{');

        for(FieldDescriptor<T> descriptor: fieldDescriptors) {
            if(!descriptor.isActive())
                continue;

            if(notFirst)
                builder.append(',');

            builder.append('\"');
            builder.append(descriptor.getPropertyName());
            builder.append('\"');
            builder.append(':');
            builder.append(serializer.serializeValue(descriptor.getValue(v)));

            notFirst = true;
        }

        builder.append('}');

        return builder.toString();
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
        private boolean active;

        public FieldDescriptor(String propertyName, Field objField) {
            this.propertyName = propertyName;
            this.field = objField;
            this.active = true;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Field getField() {
            return field;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Object getValue(T v) {
            try {
                return field.get(v);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

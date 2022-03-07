package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class JsonObject extends HashMap<String, JsonValue> implements JsonValue {

    public JsonObject() {}

    public JsonObject(Map<?, ?> map) {
        for(Entry<?, ?> entry: map.entrySet()) {
            if(entry.getValue() == null) {
                this.putNull(entry.getKey().toString());
            } else {
                this.put(entry.getKey().toString(), JsonValue.valueOf(entry.getValue()));
            }
        }
    }

    public int getInt(@NotNull String name) {
        return this.get(name).getAsInteger();
    }

    public long getLong(@NotNull String name) {
        return this.get(name).getAsLong();
    }

    public double getDouble(@NotNull String name) {
        return this.get(name).getAsDouble();
    }

    public float getFloat(@NotNull String name) {
        return this.get(name).getAsFloat();
    }

    public short getShort(@NotNull String name) {
        return this.get(name).getAsShort();
    }

    public byte getByte(@NotNull String name) {
        return this.get(name).getAsByte();
    }

    public boolean getBoolean(@NotNull String name) {
        return this.get(name).getAsBoolean();
    }

    @NotNull
    public String getString(@NotNull String name) {
        return this.get(name).getAsString();
    }

    @NotNull
    public JsonObject getObject(@NotNull String name) {
        return this.get(name).getAsObject();
    }

    @NotNull
    public JsonArray getArray(@NotNull String name) {
        return this.get(name).getAsArray();
    }

    @NotNull
    public JsonValue getNonNull(@NotNull String name) {
        JsonValue o = super.get(name);

        if(o == null)
            return JsonNull.NULL;

        return o;
    }

    public void putInt(@NotNull String name, int value) {
        this.put(name, JsonNumber.valueOf(value));
    }

    public void putLong(@NotNull String name, long value) {
        this.put(name, JsonNumber.valueOf(value));
    }

    public void putDouble(@NotNull String name, double value) {
        this.put(name, JsonNumber.valueOf(value));
    }

    public void putFloat(@NotNull String name, float value) {
        this.put(name, JsonNumber.valueOf(value));
    }

    public void putShort(@NotNull String name, short value) {
        this.put(name, JsonNumber.valueOf(value));
    }

    public void putByte(@NotNull String name, byte value) {
        this.put(name, JsonNumber.valueOf(value));
    }

    public void putBoolean(@NotNull String name, boolean value) {
        this.put(name, JsonBoolean.valueOf(value));
    }

    public void putString(@NotNull String name, @NotNull String value) {
        Objects.requireNonNull(value);
        this.put(name, JsonString.valueOf(value));
    }

    public void putString(@NotNull String name, @NotNull CharSequence value) {
        Objects.requireNonNull(value);
        this.put(name, JsonString.valueOf(value));
    }

    public void putString(@NotNull String name, @NotNull Object value) {
        Objects.requireNonNull(value);
        this.put(name, JsonString.valueOf(value));
    }

    public void putObject(@NotNull String name, @NotNull JsonObject value) {
        Objects.requireNonNull(value);
        this.put(name, value);
    }

    public void putArray(@NotNull String name, @NotNull JsonArray value) {
        Objects.requireNonNull(value);
        this.put(name, value);
    }

    public void putNull(@NotNull String name) {
        this.put(name, JsonNull.NULL);
    }

    @Override
    public JsonValue put(String key, JsonValue value) {
        return super.put(Objects.requireNonNull(key), value==null?JsonNull.NULL:value);
    }

    public boolean presentNotNull(String key) {
        JsonValue value = get(key);
        return value != null && !value.isNull();
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public JsonObject getAsObject() {
        return this;
    }

    @Override
    public String getType() {
        if(this.getClass() == JsonObject.class) {
            return "JsonObject";
        } else {
            return this.getClass().getName();
        }
    }

    public static JsonObject valueOf(Map<?, ?> map) {
        return new JsonObject(map);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if(o instanceof JsonObject obj) {
            if(obj.size() != this.size())
                return false;

            for(Entry<String, JsonValue> entry: obj.entrySet()) {
                JsonValue value = this.get(entry.getKey());

                if(value == null || !value.equals(entry.getValue()))
                    return false;
            }

            return true;
        }

        return false;
    }

    public static JsonObject of() {
        return new JsonObject();
    }

    public static JsonObject of(String name, Object value) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));

        return obj;
    }

    public static JsonObject of(String name, Object value, String name1, Object value1) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));
        obj.put(name1, JsonValue.valueOf(value1));
        return obj;
    }

    public static JsonObject of(String name, Object value, String name1, Object value1, String name2, Object value2) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));
        obj.put(name1, JsonValue.valueOf(value1));
        obj.put(name2, JsonValue.valueOf(value2));
        return obj;
    }

    public static JsonObject of(String name, Object value, String name1, Object value1, String name2, Object value2, String name3, Object value3) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));
        obj.put(name1, JsonValue.valueOf(value1));
        obj.put(name2, JsonValue.valueOf(value2));
        obj.put(name3, JsonValue.valueOf(value3));
        return obj;
    }

    public static JsonObject of(String name, Object value, String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));
        obj.put(name1, JsonValue.valueOf(value1));
        obj.put(name2, JsonValue.valueOf(value2));
        obj.put(name3, JsonValue.valueOf(value3));
        obj.put(name4, JsonValue.valueOf(value4));
        return obj;
    }

    public static JsonObject of(String name, Object value, String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4, String name5, Object value5) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));
        obj.put(name1, JsonValue.valueOf(value1));
        obj.put(name2, JsonValue.valueOf(value2));
        obj.put(name3, JsonValue.valueOf(value3));
        obj.put(name4, JsonValue.valueOf(value4));
        obj.put(name5, JsonValue.valueOf(value5));
        return obj;
    }

    public static JsonObject of(String name, Object value, String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4, String name5, Object value5, String name6, Object value6) {
        JsonObject obj = new JsonObject();
        obj.put(name, JsonValue.valueOf(value));
        obj.put(name1, JsonValue.valueOf(value1));
        obj.put(name2, JsonValue.valueOf(value2));
        obj.put(name3, JsonValue.valueOf(value3));
        obj.put(name4, JsonValue.valueOf(value4));
        obj.put(name5, JsonValue.valueOf(value5));
        obj.put(name6, JsonValue.valueOf(value6));
        return obj;
    }

    public static JsonObject of(String[] names, Object[] values) {
        if(names == null || values == null)
            throw new NullPointerException();
        else if(names.length != values.length)
            throw new IllegalArgumentException();

        JsonObject obj = new JsonObject();

        for(int x = 0; x < names.length; x++) {
            obj.put(names[x], JsonValue.valueOf(values[x]));
        }

        return obj;
    }

}

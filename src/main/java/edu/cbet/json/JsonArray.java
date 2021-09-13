package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class JsonArray extends ArrayList<JsonValue> implements JsonValue {

    public JsonArray() {}

    public JsonArray(int size) {
        super(size);
    }

    public JsonArray(Collection<?> collection) {
        for(Object o: collection) {
            if(o == null)
                this.add(JsonNull.NULL);
            else
                this.add(JsonValue.valueOf(o));
        }
    }

    public JsonArray(Object[] objects) {
        this(objects.length);

        for(Object o: objects) {
            this.add(JsonValue.valueOf(o));
        }
    }

    public int getInt(int index) {
        return this.get(index).getAsInteger();
    }

    public long getLong(int index) {
        return this.get(index).getAsLong();
    }

    public double getDouble(int index) {
        return this.get(index).getAsDouble();
    }

    public float getFloat(int index) {
        return this.get(index).getAsFloat();
    }

    public short getShort(int index) {
        return this.get(index).getAsShort();
    }

    public byte getByte(int index) {
        return this.get(index).getAsByte();
    }

    public boolean getBoolean(int index) {
        return this.get(index).getAsBoolean();
    }

    @NotNull
    public String getString(int index) {
        return this.get(index).getAsString();
    }

    @NotNull
    public JsonObject getObject(int index) {
        return this.get(index).getAsObject();
    }

    @NotNull
    public JsonArray getArray(int index) {
        return this.get(index).getAsArray();
    }

    public void setInt(int index, int value) {
        this.set(index, JsonNumber.valueOf(value));
    }

    public void setLong(int index, long value) {
        this.set(index, JsonNumber.valueOf(value));
    }

    public void setDouble(int index, double value) {
        this.set(index, JsonNumber.valueOf(value));
    }

    public void setFloat(int index, float value) {
        this.set(index, JsonNumber.valueOf(value));
    }

    public void setShort(int index, short value) {
        this.set(index, JsonNumber.valueOf(value));
    }

    public void setByte(int index, byte value) {
        this.set(index, JsonNumber.valueOf(value));
    }

    public void setBoolean(int index, boolean value) {
        this.set(index, JsonBoolean.valueOf(value));
    }

    public void setString(int index, @NotNull String value) {
        Objects.requireNonNull(value);
        this.set(index, JsonString.valueOf(value));
    }

    public void setString(int index, @NotNull CharSequence value) {
        Objects.requireNonNull(value);
        this.set(index, JsonString.valueOf(value));
    }

    public void setString(int index, @NotNull Object value) {
        Objects.requireNonNull(value);
        this.set(index, JsonString.valueOf(value));
    }

    public void setObject(int index, @NotNull JsonObject value) {
        Objects.requireNonNull(value);
        this.set(index, value);
    }

    public void setArray(int index, @NotNull JsonArray value) {
        Objects.requireNonNull(value);
        this.set(index, value);
    }

    public void setNull(int index) {
        this.set(index, JsonNull.NULL);
    }

    public void addInt(int value) {
        this.add(JsonNumber.valueOf(value));
    }

    public void addLong(long value) {
        this.add(JsonNumber.valueOf(value));
    }

    public void addDouble(double value) {
        this.add(JsonNumber.valueOf(value));
    }

    public void addFloat(float value) {
        this.add(JsonNumber.valueOf(value));
    }

    public void addShort(short value) {
        this.add(JsonNumber.valueOf(value));
    }

    public void addByte(byte value) {
        this.add(JsonNumber.valueOf(value));
    }

    public void addBoolean(boolean value) {
        this.add(JsonBoolean.valueOf(value));
    }

    public void addString(@NotNull String value) {
        Objects.requireNonNull(value);
        this.add(JsonString.valueOf(value));
    }

    public void addString(@NotNull CharSequence value) {
        Objects.requireNonNull(value);
        this.add(JsonString.valueOf(value));
    }

    public void addString(@NotNull Object value) {
        Objects.requireNonNull(value);
        this.add(JsonString.valueOf(value));
    }

    public void addObject(@NotNull JsonObject value) {
        Objects.requireNonNull(value);
        this.add(value);
    }

    public void addArray(@NotNull JsonArray value) {
        Objects.requireNonNull(value);
        this.add(value);
    }

    public void addNull() {
        this.add(JsonNull.NULL);
    }

    @Override
    public boolean add(JsonValue value) {
        return super.add(value == null? JsonNull.NULL: value);
    }

    @Override
    public JsonValue set(int index, JsonValue value){
        return super.set(index, value == null? JsonNull.NULL: value);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JsonArray getAsArray() {
        return this;
    }

    @NotNull
    public static JsonArray valueOf(@NotNull Collection<?> collection) {
        return new JsonArray(collection);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if(o instanceof JsonArray arr) {
            if(arr.size() != this.size())
                return false;

            Iterator<JsonValue> itr = this.iterator();

            for(JsonValue v: arr) {
                if(!itr.next().equals(v))
                    return false;
            }

            return true;
        }

        return false;
    }

    public static JsonArray of() {
        return new JsonArray();
    }

    public static JsonArray of(Object... values) {
        return new JsonArray(values);
    }
}

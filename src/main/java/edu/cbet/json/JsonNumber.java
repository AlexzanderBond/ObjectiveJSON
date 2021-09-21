package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public class JsonNumber implements JsonValue{
    private final boolean isInteger;
    private final Number value;

    public JsonNumber(int value) {
        this.value = value;
        this.isInteger = true;
    }

    public JsonNumber(long value) {
        this.value = value;
        this.isInteger = true;
    }

    public JsonNumber(short value) {
        this.value = value;
        this.isInteger = true;
    }

    public JsonNumber(byte value) {
        this.value = value;
        this.isInteger = true;
    }

    public JsonNumber(float value) {
        this.value = value;
        this.isInteger = false;
    }

    public JsonNumber(double value) {
        this.value = value;
        this.isInteger = false;
    }

    public JsonNumber(@NotNull Number number) {
        this.value = Objects.requireNonNull(number);
        this.isInteger = !(number instanceof Double || number instanceof Float || number instanceof BigDecimal);
    }

    public boolean isInteger() {
        return this.isInteger;
    }

    @Override
    public String getType() {
        return "Number";
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public double getAsDouble() {
        return this.value.doubleValue();
    }

    @Override
    public float getAsFloat() {
        return this.value.floatValue();
    }

    @Override
    public int getAsInteger() {
        if(!this.isInteger)
            throw new IllegalStateException("JsonValue is a floating-point number, conversion to an integer will always be lossy");
        return this.value.intValue();
    }

    @Override
    public long getAsLong() {
        if(!this.isInteger)
            throw new IllegalStateException("JsonValue is a floating-point number, conversion to an integer will always be lossy");
        return this.value.longValue();
    }

    @Override
    public short getAsShort() {
        if (!this.isInteger)
            throw new IllegalStateException("JsonValue is a floating-point number, conversion to an integer will always be lossy");
        return this.value.shortValue();
    }

    @Override
    public byte getAsByte() {
        if(!this.isInteger)
            throw new IllegalStateException("JsonValue is a floating-point number, conversion to an integer will always be lossy");
        return this.value.byteValue();
    }

    @NotNull
    public Number getAsNumber() {
        return this.value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @NotNull
    public static JsonNumber valueOf(int i) {
        return new JsonNumber(i);
    }

    @NotNull
    public static JsonNumber valueOf(long l) {
        return new JsonNumber(l);
    }

    @NotNull
    public static JsonNumber valueOf(short s) {
        return new JsonNumber(s);
    }

    @NotNull
    public static JsonNumber valueOf(byte b) {
        return new JsonNumber(b);
    }

    @NotNull
    public static JsonNumber valueOf(float f) {
        return new JsonNumber(f);
    }

    @NotNull
    public static JsonNumber valueOf(double d) {
        return new JsonNumber(d);
    }

    @NotNull
    public static JsonNumber valueOf(Number n) {
        return new JsonNumber(n);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof JsonNumber num) {
            if(isInteger && num.isInteger) {
                return this.value.longValue() == num.value.longValue();
            } else {
                return this.value.doubleValue() == num.value.doubleValue();
            }
        } else if(obj instanceof Number num) {
            if (!this.isInteger && num instanceof Integer || num instanceof Long || num instanceof Byte || num instanceof Short) {
                return false;
            }
            return this.value.doubleValue() == num.doubleValue();
        }

        return false;
    }
}

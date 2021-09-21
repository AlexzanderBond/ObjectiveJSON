package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class JsonString implements JsonValue {
    public static final JsonString EMPTY = new JsonString("");

    public static final byte NUMBER_MASK      = 0b00000001;
    public static final byte INTEGER_MASK     = 0b00000010;
    public static final byte BOOLEAN_MASK     = 0b00000100;
    public static final byte NUMBER_TEST_MASK = 0b00000011;
    private final String value;
    private byte mask = -1;

    public JsonString(@NotNull String value) {
        this.value = Objects.requireNonNull(value);
    }

    public JsonString(@NotNull CharSequence value) {
        this(value.toString());
    }

    public JsonString(@NotNull Object value) {
        this(value.toString());

        if(value instanceof Number) {
            if(value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
                this.mask = NUMBER_MASK;
            } else if(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
                this.mask = INTEGER_MASK | NUMBER_MASK;
            }
        } else if(value instanceof Boolean) {
            this.mask = BOOLEAN_MASK;
        }
    }

    @NotNull
    @Override
    public String getAsString() {
        return this.value;
    }

    @Override
    public long getAsLong() {
        return Long.parseLong(this.value);
    }

    @Override
    public int getAsInteger() {
        return Integer.parseInt(this.value);
    }

    @Override
    public short getAsShort() {
        return Short.parseShort(this.value);
    }

    @Override
    public float getAsFloat() {
        return Float.parseFloat(this.value);
    }

    @Override
    public byte getAsByte() {
        return Byte.parseByte(this.value);
    }

    @Override
    public double getAsDouble() {
        return Double.parseDouble(this.value);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public Number getAsNumber() {
        if(isInteger()) {
            return Long.parseLong(this.value);
        } else if(isNumber()) {
            return Double.parseDouble(this.value);
        } else {
            throw new IllegalStateException("JsonValue is of type '%s' not a number".formatted(getType()));
        }
    }

    @Override
    public boolean isInteger() {
        if(mask == -1) {
            calculateMask();
        }
        return (mask & INTEGER_MASK) != 0;
    }

    @Override
    public boolean isNumber() {
        if(mask == -1) {
            calculateMask();
        }
        return (mask & NUMBER_MASK) != 0;
    }

    @Override
    public boolean isBoolean() {
        if(mask == -1) {
            calculateMask();
        }
        return (mask & BOOLEAN_MASK) != 0;
    }

    @Override
    public String getType() {
        return "String";
    }

    private void calculateMask() {
        byte mask = NUMBER_TEST_MASK;

        for(int x = 0; x < this.value.length(); x++) {
            char c = this.value.charAt(x);
            if(c == '.') {
                mask ^= INTEGER_MASK;
            } else if(!Character.isDigit(c)) {
                mask = 0;
                break;
            }
        }

        if(mask == 0) {
            if(value.equals("true") || value.equals("false")) {
                mask = BOOLEAN_MASK;
            }
        }

        this.mask = mask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof JsonString str) {
            return str.value.equals(this.value);
        } else if(o instanceof String str) {
            return str.equals(this.value);
        } else if(o instanceof CharSequence sequence) {
            return sequence.equals(this.value);
        }

        return false;
    }

    @Override
    public String toString() {
        return value;
    }

    @NotNull
    public static JsonString valueOf(@NotNull String value) {
        if(value.length() == 0)
            return EMPTY;
        return new JsonString(value);
    }

    @NotNull
    public static JsonString valueOf(@NotNull CharSequence value) {
        if(value.length() == 0)
            return EMPTY;
        return new JsonString(value);
    }

    @NotNull
    public static JsonString valueOf(@NotNull Object obj) {
        return new JsonString(obj);
    }

    @NotNull
    public static JsonString valueOf(@NotNull Character c) {
        return new JsonString(String.valueOf(c));
    }
}

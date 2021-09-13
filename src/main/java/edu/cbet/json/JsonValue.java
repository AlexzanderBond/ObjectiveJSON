package edu.cbet.json;

import java.util.Collection;
import java.util.Map;

public interface JsonValue {
    default boolean isInteger() {
        return false;
    }

    default boolean isNumber() {
        return false;
    }

    default boolean isBoolean() {
        return false;
    }

    default boolean isString() {
        return false;
    }

    default boolean isObject() {
        return false;
    }

    default boolean isArray() {
        return false;
    }

    default boolean isNull() {
        return false;
    }

    default String getType() {
        return this.getClass().getName();
    }

    default boolean getAsBoolean() {
        throw new IllegalStateException("JsonValue is of type '%s' not a boolean".formatted(getType()));
    }

    default long getAsLong() {
        throw new IllegalStateException("JsonValue is of type '%s' not a long".formatted(getType()));
    }

    default int getAsInteger() {
        throw new IllegalStateException("JsonValue is of type '%s' not an integer".formatted(getType()));
    }

    default short getAsShort() {
        throw new IllegalStateException("JsonValue is of type '%s' not a short".formatted(getType()));
    }

    default byte getAsByte() {
        throw new IllegalStateException("JsonValue is of type '%s' not a byte".formatted(getType()));
    }

    default double getAsDouble() {
        throw new IllegalStateException("JsonValue is of type '%s' not a double".formatted(getType()));
    }

    default float getAsFloat() {
        throw new IllegalStateException("JsonValue is of type '%s' not a float".formatted(getType()));
    }

    default String getAsString() {
        throw new IllegalStateException("JsonValue is of type '%s' not a String".formatted(getType()));
    }

    default JsonObject getAsObject() {
        throw new IllegalStateException("JsonValue is of type '%s' not an object".formatted(getType()));
    }

    default JsonArray getAsArray() {
        throw new IllegalStateException("JsonValue is of type '%s' not an array".formatted(getType()));
    }

    static JsonValue valueOf(Object o) {
        if(o == null) {
            return JsonNull.NULL;
        } else if(o instanceof JsonValue j) {
            return j;
        }

        if(o instanceof Number n) {
            return JsonNumber.valueOf(n);
        } else if(o instanceof Boolean b) {
            return JsonBoolean.valueOf(b);
        } else if(o instanceof String s) {
            return JsonString.valueOf(s);
        } else if(o instanceof Collection<?> c) {
            return JsonArray.valueOf(c);
        } else if(o instanceof CharSequence cs) {
            return JsonString.valueOf(cs);
        } else if(o instanceof Map<?, ?> m) {
            return JsonObject.valueOf(m);
        }

        throw new IllegalArgumentException("Couldn't provide a value for type " + o.getClass().getName());
    }
}

package edu.cbet.json;

import org.jetbrains.annotations.NotNull;

public class JsonBoolean implements JsonValue {
    public static final JsonBoolean TRUE = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private final boolean value;

    public JsonBoolean(boolean bool) {
        this.value = bool;
    }

    @Override
    public boolean getAsBoolean() {
        return this.value;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public String getType() {
        return "Boolean";
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(o instanceof JsonBoolean bool) {
            return bool.value == this.value;
        } else if(o instanceof Boolean bool) {
            return bool == this.value;
        }

        return false;
    }

    @NotNull
    public static JsonBoolean valueOf(boolean b) {
        return b? TRUE: FALSE;
    }
}

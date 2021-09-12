package edu.cbet.json;

public class JsonNull implements JsonValue{
    public static final JsonNull NULL = new JsonNull();

    private JsonNull() {}

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getType() {
        return "Null";
    }

    @Override
    public String toString() {
        return "JsonNull";
    }

    @Override
    public boolean equals(Object obj) {
        return obj == null || obj instanceof JsonNull;
    }
}

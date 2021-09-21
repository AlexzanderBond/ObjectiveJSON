package edu.cbet.json;


public interface Modifier<T> {
    void modify(T value, JsonValue json);
}

package edu.cbet.json;


import java.util.Map;

public interface Modifier<T> {
    void modify(T value, Map<String, Object> map);
}

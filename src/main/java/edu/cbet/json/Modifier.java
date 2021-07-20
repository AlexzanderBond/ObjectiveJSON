package edu.cbet.json;

import java.util.HashMap;

public interface Modifier<T> {
    void modify(T value, HashMap<String, String> map);
}

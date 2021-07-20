package edu.cbet.json;

import java.util.HashMap;

/**
 * The base class for any Java object > Json object serializer
 * @param <T> the type to serialize
 */
public interface JsonSerializer<T> {
    HashMap<String, String> getSerializedValue(ObjectSerializer serializer, T v);
    void addFilter(Filter filter);
}

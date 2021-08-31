package edu.cbet.json;

import java.util.Map;

/**
 * The base class for any Java object > Json object serializer
 * @param <T> the type to serialize
 */
public interface JsonSerializer<T> {
    Map<String, Object> getSerializedValue(ObjectSerializer serializer, T v);
    void addFilter(Filter filter);
}

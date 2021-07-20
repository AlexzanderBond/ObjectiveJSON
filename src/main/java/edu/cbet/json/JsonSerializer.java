package edu.cbet.json;

/**
 * The base class for any Java object > Json object serializer
 * @param <T> the type to serialize
 */
public interface JsonSerializer<T> {
    String getSerializedValue(ObjectSerializer serializer, T v);
    void addFilter(Filter filter);
}

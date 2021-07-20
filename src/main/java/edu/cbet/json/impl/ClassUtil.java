package edu.cbet.json.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Class utilities relating to primitive types and wrappers
 *
 * For the sake of speed and simplicity a primitive type is a number, letter, or boolean that is not the wrapper type, and a wrapper is the wrapper type for a number, letter of boolean.
 * I full well know that other primitive types such as void and wrapper types such as Void exist, but they are not as useful.
 *
 * A full list of the primitive/wrapper types will be the following:
 *
 * Long
 * Integer
 * Short
 * Byte
 * Double
 * Float
 * Character
 * Boolean
 *
 */
public class ClassUtil {
    public static final Class<?>[] PRIMITIVE_ARRAY_CLASSES = {long[].class, int[].class, short[].class, byte[].class, char[].class, boolean[].class, float[].class, double[].class};

    public static Class<?> getWrapperClass(Class<?> clazz) {
        if(clazz == long.class) {
            return Long.class;
        } else if(clazz == int.class) {
            return Integer.class;
        } else if(clazz == short.class) {
            return Short.class;
        } else if(clazz == byte.class) {
            return Byte.class;
        } else if(clazz == char.class) {
            return Character.class;
        } else if(clazz == boolean.class) {
            return Boolean.class;
        } else if(clazz == float.class) {
            return Float.class;
        } else if(clazz == double.class) {
            return Double.class;
        } else {
            throw new IllegalArgumentException("Not a primitive(Non-array) type");
        }
    }

    public static boolean isWrapperClass(Class<?> clazz) {
        return (clazz == Long.class) ||
                (clazz == Integer.class) ||
                (clazz == Short.class) ||
                (clazz == Byte.class) ||
                (clazz == Character.class) ||
                (clazz == Boolean.class) ||
                (clazz == Float.class) ||
                (clazz == Double.class);
    }

    public static Class<?> getPrimitiveClass(Class<?> clazz) {
        if(clazz == Long.class) {
            return long.class;
        } else if(clazz == Integer.class) {
            return int.class;
        } else if(clazz == Short.class) {
            return short.class;
        } else if(clazz == Byte.class) {
            return byte.class;
        } else if(clazz == Character.class) {
            return char.class;
        } else if(clazz == Boolean.class) {
            return boolean.class;
        } else if(clazz == Float.class) {
            return float.class;
        } else if(clazz == Double.class) {
            return double.class;
        } else {
            throw new IllegalArgumentException("Not a wrapper type");
        }
    }

    public static boolean isPrimitiveClass(Class<?> clazz) {
        return (clazz == long.class) ||
                (clazz == int.class) ||
                (clazz == short.class) ||
                (clazz == byte.class) ||
                (clazz == char.class) ||
                (clazz == boolean.class) ||
                (clazz == float.class) ||
                (clazz == double.class);
    }
}

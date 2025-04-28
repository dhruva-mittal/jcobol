package org.jcobol.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility class for converting between COBOL and Java types
 */
public class CobolTypeConverter {

    /**
     * Convert a string value to the appropriate Java type.
     *
     * @param value The string value
     * @param targetType The target Java type
     * @return The converted value
     */
    public Object convertToNumericType(String value, Class<?> targetType) {
        if (value == null) {
            value = "0";
        }
        
        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value.trim());
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value.trim());
        } else if (targetType == Short.class || targetType == short.class) {
            return Short.parseShort(value.trim());
        } else if (targetType == Byte.class || targetType == byte.class) {
            return Byte.parseByte(value.trim());
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value.trim());
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value.trim());
        } else if (targetType == BigDecimal.class) {
            return new BigDecimal(value.trim());
        } else if (targetType == BigInteger.class) {
            return new BigInteger(value.trim());
        } else {
            return value; // Default to string
        }
    }
    
    /**
     * Convert a numeric value to the appropriate Java type.
     *
     * @param value The numeric value
     * @param targetType The target Java type
     * @return The converted value
     */
    public Object convertToNumericType(Number value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            return value.intValue();
        } else if (targetType == Long.class || targetType == long.class) {
            return value.longValue();
        } else if (targetType == Short.class || targetType == short.class) {
            return value.shortValue();
        } else if (targetType == Byte.class || targetType == byte.class) {
            return value.byteValue();
        } else if (targetType == Double.class || targetType == double.class) {
            return value.doubleValue();
        } else if (targetType == Float.class || targetType == float.class) {
            return value.floatValue();
        } else if (targetType == BigDecimal.class) {
            return new BigDecimal(value.toString());
        } else if (targetType == BigInteger.class) {
            if (value instanceof BigInteger) {
                return value;
            } else {
                return BigInteger.valueOf(value.longValue());
            }
        } else {
            return value.toString(); // Default to string
        }
    }
}
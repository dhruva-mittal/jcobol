package org.jcobol.core.utils;

import org.jcobol.annotation.CobolField;
import org.jcobol.enums.CobolFieldType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility class for providing default values for COBOL fields
 */
public class CobolDefaultValueProvider {

    /**
     * Get default value based on COBOL data type and target Java type.
     *
     * @param cobolField The CobolField annotation
     * @param targetType The target Java type
     * @return The default value for the field
     */
    public static Object getDefaultValue(CobolField cobolField, Class<?> targetType) {
        CobolFieldType type = cobolField.type();
        int length = cobolField.length();
        int scale = cobolField.scale();

        // For string fields, return string default
        if (targetType == String.class) {
            switch (type) {
                case ALPHANUMERIC:
                    // Alphanumeric - fill with spaces
                    return repeatString(" ", length);
                    
                case NUMERIC:
                    // Numeric - fill with zeros
                    return repeatString("0", length);
                    
                case DECIMAL_ASSUMED:
                    // Handle assumed decimal points
                    return repeatString("0", length);
                    
                case DECIMAL_EXPLICIT:
                    // Handle explicit decimal points - note that length doesn't include the decimal point
                    if (scale > 0) {
                        String intPart = repeatString("0", length - scale - 1);
                        String decimalPart = repeatString("0", scale);
                        return intPart + "." + decimalPart;
                    }
                    return repeatString("0", length);
                    
                default:
                    // Default to spaces if type is unknown
                    return repeatString(" ", length);
            }
        }
        
        // For numeric fields, return appropriate numeric types
        if (targetType == Integer.class || targetType == int.class) {
            return 0;
        } else if (targetType == Long.class || targetType == long.class) {
            return 0L;
        } else if (targetType == Short.class || targetType == short.class) {
            return (short) 0;
        } else if (targetType == Byte.class || targetType == byte.class) {
            return (byte) 0;
        } else if (targetType == Double.class || targetType == double.class) {
            return 0.0;
        } else if (targetType == Float.class || targetType == float.class) {
            return 0.0f;
        } else if (targetType == BigDecimal.class) {
            return new BigDecimal("0");
        } else if (targetType == BigInteger.class) {
            return BigInteger.ZERO;
        }
        
        // Default to string representation
        return getDefaultValue(cobolField, String.class);
    }

    /**
     * Create a string by repeating a character.
     *
     * @param str The string to repeat
     * @param count The number of times to repeat
     * @return The repeated string
     */
    public static String repeatString(String str, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(str);
        }
        return builder.toString();
    }
}
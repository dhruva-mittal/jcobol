package org.jcobol.core.handlers;

import org.jcobol.annotation.CobolField;
import org.jcobol.core.utils.CobolFieldCalculator;
import org.jcobol.core.utils.CobolTypeConverter;
import org.jcobol.exception.CobolParseException;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Handler for COMP (computational) COBOL fields
 */
public class CompFieldHandler {

    private final CobolFieldCalculator calculator = new CobolFieldCalculator();
    private final CobolTypeConverter converter = new CobolTypeConverter();

    /**
     * Extract a COMP (binary) value from byte data.
     *
     * @param data The binary data
     * @param fieldType The Java field type
     * @param cobolField The CobolField annotation
     * @return The extracted value
     * @throws CobolParseException If the data cannot be parsed correctly
     */
    public Object extractValue(byte[] data, Class<?> fieldType, CobolField cobolField) 
            throws CobolParseException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN); // COBOL typically uses big-endian
        
        // COMP fields are binary representations
        int length = cobolField.length();
        
        try {
            // Handle different field sizes
            if (length <= 2) {
                short value = buffer.getShort(0);
                return converter.convertToNumericType(value, fieldType);
            } else if (length <= 4) {
                int value = buffer.getInt(0);
                return converter.convertToNumericType(value, fieldType);
            } else if (length <= 8) {
                long value = buffer.getLong(0);
                return converter.convertToNumericType(value, fieldType);
            } else {
                // For larger values, use BigInteger
                BigInteger value = new BigInteger(data);
                return converter.convertToNumericType(value, fieldType);
            }
        } catch (Exception e) {
            throw new CobolParseException("Error extracting COMP value: " + e.getMessage(), e);
        }
    }
    
    /**
     * Write a COMP (binary) value to a byte array.
     *
     * @param value The field value
     * @param data The byte array to write to
     * @param offset The offset position in the byte array
     * @param cobolField The CobolField annotation
     * @return The number of bytes written
     * @throws CobolParseException If there's an error during conversion
     */
    public int writeValue(Object value, byte[] data, int offset, CobolField cobolField) 
            throws CobolParseException {
        try {
            int length = calculator.calculateBinaryLength(cobolField);
            ByteBuffer buffer = ByteBuffer.allocate(length);
            buffer.order(ByteOrder.BIG_ENDIAN); // COBOL typically uses big-endian
            
            // Convert value based on length
            if (length <= 2) {
                short shortValue = value == null ? 0 : 
                    (value instanceof Number ? ((Number) value).shortValue() : 
                    Short.parseShort(value.toString()));
                buffer.putShort(shortValue);
            } else if (length <= 4) {
                int intValue = value == null ? 0 : 
                    (value instanceof Number ? ((Number) value).intValue() : 
                    Integer.parseInt(value.toString()));
                buffer.putInt(intValue);
            } else {
                long longValue = value == null ? 0 : 
                    (value instanceof Number ? ((Number) value).longValue() : 
                    Long.parseLong(value.toString()));
                buffer.putLong(longValue);
            }
            
            // Copy from buffer to output array
            System.arraycopy(buffer.array(), 0, data, offset, length);
            return length;
        } catch (Exception e) {
            throw new CobolParseException("Error writing COMP value: " + e.getMessage(), e);
        }
    }
}
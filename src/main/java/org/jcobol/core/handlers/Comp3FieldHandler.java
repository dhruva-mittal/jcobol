package org.jcobol.core.handlers;

import org.jcobol.annotation.CobolField;
import org.jcobol.core.utils.CobolFieldCalculator;
import org.jcobol.core.utils.CobolTypeConverter;
import org.jcobol.exception.CobolParseException;

/**
 * Handler for COMP-3 (packed decimal) COBOL fields
 */
public class Comp3FieldHandler {

    private final CobolFieldCalculator calculator = new CobolFieldCalculator();
    private final CobolTypeConverter converter = new CobolTypeConverter();

    /**
     * Extract a COMP-3 (packed decimal) value from byte data.
     *
     * @param data The binary data
     * @param fieldType The Java field type
     * @param cobolField The CobolField annotation
     * @return The extracted value
     * @throws CobolParseException If the data cannot be parsed correctly
     */
    public Object extractValue(byte[] data, Class<?> fieldType, CobolField cobolField)
            throws CobolParseException {
        try {
            StringBuilder valueStr = new StringBuilder();
            boolean negative = false;

            // Process all bytes except the last one
            for (int i = 0; i < data.length - 1; i++) {
                byte b = data[i];
                // Each byte contains two digits, one in each nibble
                valueStr.append((b >> 4) & 0xF); // High nibble
                valueStr.append(b & 0xF);        // Low nibble
            }

            // Process the last byte - contains the last digit and the sign
            byte lastByte = data[data.length - 1];
            if(cobolField.length() > (data.length - 1) * 2){
                valueStr.append((lastByte >> 4) & 0xF); // High nibble is the last digit
            }

            // Low nibble of last byte is the sign
            byte sign = (byte) (lastByte & 0xF);
            negative = (sign == 0xD); // 0xD indicates negative in COMP-3

            // Create numeric value with sign
            String numStr = (negative ? "-" : "") + valueStr.toString();

            // Handle decimal point if needed
            if (cobolField.scale() > 0) {
                int insertPos = numStr.length() - cobolField.scale();
                if (insertPos >= 0) {
                    numStr = numStr.substring(0, insertPos) + "." + numStr.substring(insertPos);
                }
            }

            return converter.convertToNumericType(numStr, fieldType);
        } catch (Exception e) {
            throw new CobolParseException("Error extracting COMP-3 value: " + e.getMessage(), e);
        }
    }
    
    /**
     * Write a COMP-3 (packed decimal) value to a byte array.
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
            int declaredLength = cobolField.length();
            
            // Convert value to string representation
            String strValue = value == null ? "0" : value.toString();
            
            // Remove decimal point if present
            strValue = strValue.replace(".", "");
            
            // Determine sign
            boolean negative = strValue.startsWith("-");
            if (negative) {
                strValue = strValue.substring(1);
            }
            
            // Ensure proper length by padding with zeros
            while (strValue.length() < declaredLength) {
                strValue = "0" + strValue;
            }
            
            // Truncate if too long
            if (strValue.length() > declaredLength) {
                strValue = strValue.substring(strValue.length() - declaredLength);
            }
            
            // Convert to packed decimal format
            for (int i = 0; i < length - 1; i++) {
                int pos = i * 2;
                byte highNibble = (byte) (Character.digit(
                    pos < strValue.length() ? strValue.charAt(pos) : '0', 10) & 0xF);
                byte lowNibble = (byte) (Character.digit(
                    pos + 1 < strValue.length() ? strValue.charAt(pos + 1) : '0', 10) & 0xF);
                
                data[offset + i] = (byte) ((highNibble << 4) | lowNibble);
            }
            
            // Last byte has sign in the lower nibble
            int lastPos = (length - 1) * 2;
            byte lastDigit = (byte) (Character.digit(
                lastPos < strValue.length() ? strValue.charAt(lastPos) : '0', 10) & 0xF);
            byte signNibble = (byte) (negative ? 0x0D : 0x0C); // 0x0D for negative, 0x0C for positive
            
            data[offset + length - 1] = (byte) ((lastDigit << 4) | signNibble);
            
            return length;
        } catch (Exception e) {
            throw new CobolParseException("Error writing COMP-3 value: " + e.getMessage(), e);
        }
    }
}
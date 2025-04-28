package org.jcobol.core.handlers;

import org.jcobol.annotation.CobolField;
import org.jcobol.core.utils.CobolFieldCalculator;
import org.jcobol.core.utils.CobolTypeConverter;
import org.jcobol.enums.CobolFieldType;
import org.jcobol.exception.CobolParseException;

/**
 * Handler for standard (non-COMP) COBOL fields
 */
public class StandardFieldHandler {

    private final CobolFieldCalculator calculator = new CobolFieldCalculator();
    private final CobolTypeConverter converter = new CobolTypeConverter();

    /**
     * Extract a standard field value from byte data.
     *
     * @param data The binary data
     * @param fieldType The Java field type
     * @param cobolField The CobolField annotation
     * @return The extracted value
     * @throws CobolParseException If the data cannot be parsed correctly
     */
    public Object extractValue(byte[] data, Class<?> fieldType, CobolField cobolField) 
            throws CobolParseException {
        CobolFieldType type = cobolField.type();
        
        try {
            // Regular character fields
            switch (type) {
                case ALPHANUMERIC:
                    // Simple string extraction
                    return new String(data);
                    
                case NUMERIC:
                    // Extract numeric value (may be signed)
                    if (cobolField.signed()) {
                        return extractSignedNumeric(data, fieldType, cobolField);
                    } else {
                        String numStr = new String(data);
                        return converter.convertToNumericType(numStr, fieldType);
                    }
                    
                case DECIMAL_ASSUMED:
                    // Extract decimal with assumed decimal point (not in the data)
                    return extractDecimalValue(data, fieldType, cobolField, false);
                    
                case DECIMAL_EXPLICIT:
                    // Extract decimal with explicit decimal point (in the data)
                    return extractDecimalValue(data, fieldType, cobolField, true);
                    
                default:
                    // Default to string extraction
                    return new String(data);
            }
        } catch (Exception e) {
            throw new CobolParseException("Error extracting standard field value: " + e.getMessage(), e);
        }
    }

    /**
     * Extract a signed numeric value from byte data.
     */
    private Object extractSignedNumeric(byte[] data, Class<?> fieldType, CobolField cobolField) 
            throws CobolParseException {
        try {
            // Handle overpunch sign (last digit has sign information)
            String rawStr = new String(data);
            boolean negative = false;
            StringBuilder numStr = new StringBuilder();
            
            // Process all characters except the last one
            for (int i = 0; i < rawStr.length() - 1; i++) {
                char c = rawStr.charAt(i);
                if (Character.isDigit(c)) {
                    numStr.append(c);
                }
            }
            
            // Process the last character which may contain sign information
            char lastChar = rawStr.charAt(rawStr.length() - 1);
            
            // Check for overpunch sign
            if (lastChar >= 'p' && lastChar <= 'y') {
                // Negative with digit 0-9
                negative = true;
                numStr.append((char) (lastChar - 'p' + '0'));
            } else if (lastChar >= 'A' && lastChar <= 'I') {
                // Positive with digit 1-9
                numStr.append((char) (lastChar - 'A' + '1'));
            } else if (lastChar == '{') {
                // Positive with digit 0
                numStr.append('0');
            } else if (Character.isDigit(lastChar)) {
                // Regular digit without sign
                numStr.append(lastChar);
            }
            
            String finalStr = (negative ? "-" : "") + numStr.toString();
            return converter.convertToNumericType(finalStr, fieldType);
        } catch (Exception e) {
            throw new CobolParseException("Error extracting signed numeric: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract a decimal value from byte data.
     */
    private Object extractDecimalValue(byte[] data, Class<?> fieldType, CobolField cobolField, boolean isExplicit) 
            throws CobolParseException {
        try {
            String rawStr = new String(data);
            int scale = cobolField.scale();
            boolean signed = cobolField.signed();
            boolean negative = false;
            
            // Handle sign if needed
            if (signed) {
                // Check last character for sign
                char lastChar = rawStr.charAt(rawStr.length() - 1);
                if ((lastChar >= 'p' && lastChar <= 'y') || lastChar == '-') {
                    negative = true;
                    // Replace last digit with proper digit if needed
                    if (lastChar >= 'p' && lastChar <= 'y') {
                        rawStr = rawStr.substring(0, rawStr.length() - 1) + 
                                (char)(lastChar - 'p' + '0');
                    } else {
                        rawStr = rawStr.substring(0, rawStr.length() - 1);
                    }
                }
            }
            
            // For explicit decimal, the decimal point is already in the data
            if (!isExplicit && scale > 0) {
                // Insert decimal point based on scale for assumed decimal
                int insertPos = rawStr.length() - scale;
                if (insertPos >= 0) {
                    rawStr = rawStr.substring(0, insertPos) + "." + rawStr.substring(insertPos);
                }
            }
            
            // Apply sign if needed
            if (negative) {
                rawStr = "-" + rawStr;
            }
            
            return converter.convertToNumericType(rawStr, fieldType);
        } catch (Exception e) {
            throw new CobolParseException("Error extracting decimal value: " + e.getMessage(), e);
        }
    }
    
    /**
     * Write a standard field value to a byte array.
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
            int length = cobolField.length();
            CobolFieldType type = cobolField.type();
            boolean signed = cobolField.signed();
            int scale = cobolField.scale();
            
            // Convert value to string representation
            String strValue = value == null ? "" : value.toString();
            
            // Process based on field type
            switch (type) {
                case ALPHANUMERIC:
                    // Left-justify and pad with spaces
                    writeAlphanumericValue(strValue, data, offset, length);
                    break;
                    
                case NUMERIC:
                    // Write numeric value - handles sign if needed
                    writeNumericValue(strValue, data, offset, length, signed, false, 0);
                    break;
                    
                case DECIMAL_ASSUMED:
                    // Assumed decimal - don't include decimal point in output
                    // First remove any decimal point from the string
                    String noDecimalStr = strValue.replace(".", "");
                    
                    // Ensure proper length with implied decimal
                    writeNumericValue(noDecimalStr, data, offset, length, signed, false, scale);
                    break;
                    
                case DECIMAL_EXPLICIT:
                    // Explicit decimal - include decimal point in output
                    writeNumericValue(strValue, data, offset, length, signed, true, scale);
                    break;
                    
                default:
                    // Default to alphanumeric handling
                    writeAlphanumericValue(strValue, data, offset, length);
            }
            
            return length;
        } catch (Exception e) {
            throw new CobolParseException("Error writing regular field: " + e.getMessage(), e);
        }
    }
    
    /**
     * Write an alphanumeric value to a byte array.
     */
    private void writeAlphanumericValue(String value, byte[] data, int offset, int length) {
        byte[] bytes = value.getBytes();
        int copyLength = Math.min(bytes.length, length);
        
        // Copy actual data
        System.arraycopy(bytes, 0, data, offset, copyLength);
        
        // Fill remaining space with spaces
        for (int i = copyLength; i < length; i++) {
            data[offset + i] = (byte) ' '; // Space character
        }
    }
    
    /**
     * Write a numeric value to a byte array with proper formatting.
     */
    private void writeNumericValue(String strValue, byte[] data, int offset, 
            int length, boolean signed, boolean explicitDecimal, int scale) 
            throws CobolParseException {
        try {
            // Detect sign
            boolean hasNegative = strValue.startsWith("-");
            boolean hasPlus = strValue.startsWith("+");
            String valueWithoutSign = hasNegative ? strValue.substring(1) :
                                      (hasPlus ? strValue.substring(1) : strValue);
            
            // Handle decimal point
            int decimalPos = valueWithoutSign.indexOf('.');
            String beforeDecimal = decimalPos >= 0 ? valueWithoutSign.substring(0, decimalPos) : valueWithoutSign;
            String afterDecimal = decimalPos >= 0 ? valueWithoutSign.substring(decimalPos + 1) : "";
            
            StringBuilder formattedValue = new StringBuilder();
    
            if (hasNegative) {
                formattedValue.append('-');
            } else if (hasPlus) {
                formattedValue.append('+');
            }
            
            // Format according to decimal type
            if (explicitDecimal && scale > 0) {
                formatExplicitDecimal(formattedValue, beforeDecimal, afterDecimal, length, scale);
            } else {
                formatAssumedDecimal(formattedValue, beforeDecimal, afterDecimal, length);
            }
    
            // Convert to bytes
            byte[] numBytes = formattedValue.toString().getBytes();
            
            // Handle sign if needed (for non-explicit decimal)
            if (signed && length > 0) {
                if (hasNegative) {
                    // In COBOL overpunch notation, replace the last digit with a letter
                    // p-y represent digits 0-9 with negative sign
                    int lastDigitPos = length - 1;
                    byte lastDigit = numBytes[lastDigitPos];
                    if (lastDigit >= '0' && lastDigit <= '9') {
                        data[offset + lastDigitPos] = (byte) ('p' + (lastDigit - '0'));
                    } else {
                        System.arraycopy(numBytes, 0, data, offset, Math.min(numBytes.length, length));
                    }
                } else {
                    System.arraycopy(numBytes, 0, data, offset, Math.min(numBytes.length, length));
                }
            } else {
                System.arraycopy(numBytes, 0, data, offset, Math.min(numBytes.length, length));
            }
        } catch (Exception e) {
            throw new CobolParseException("Error formatting numeric value: " + e.getMessage(), e);
        }
    }
    
    /**
     * Format a value with explicit decimal point.
     */
    private void formatExplicitDecimal(StringBuilder formattedValue, String beforeDecimal, 
            String afterDecimal, int length, int scale) {
        // Format before decimal portion
        while (formattedValue.length() + beforeDecimal.length() < (length - scale - 1)) {
            formattedValue.append('0');
        }
        
        // Add integer part
        formattedValue.append(beforeDecimal);
        
        // Add decimal point
        formattedValue.append('.');
        
        // Add fractional part
        formattedValue.append(afterDecimal);
        
        // Pad with zeros if needed
        while (formattedValue.length() < length) {
            formattedValue.append('0');
        }
        
        // Truncate if too long
        if (formattedValue.length() > length) {
            formattedValue.setLength(length);
        }
    }
    
    /**
     * Format a value with assumed decimal point (no actual decimal in output).
     */
    private void formatAssumedDecimal(StringBuilder formattedValue, String beforeDecimal, 
            String afterDecimal, int length) {
        // For assumed decimal, we don't include the decimal point
        // Combine the parts without the decimal
        String combined = beforeDecimal + afterDecimal;
        
        // Pad with zeros to match required length
        while (formattedValue.length() + combined.length() < length) {
            formattedValue.append('0');
        }
        
        // Add the actual digits
        formattedValue.append(combined);
        
        // Truncate if too long
        if (formattedValue.length() > length) {
            formattedValue.setLength(length);
        }
    }
}
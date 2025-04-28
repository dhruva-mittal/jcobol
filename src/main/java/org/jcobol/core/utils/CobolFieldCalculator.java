package org.jcobol.core.utils;

import org.jcobol.annotation.CobolField;

/**
 * Utility class for calculating COBOL field measurements
 */
public class CobolFieldCalculator {

    /**
     * Calculate the binary length of a COBOL field.
     * This accounts for COMP and COMP-3 fields which may have different storage lengths.
     *
     * @param cobolField The CobolField annotation
     * @return The binary length of the field
     */
    public int calculateBinaryLength(CobolField cobolField) {
        int declaredLength = cobolField.length();
        
        // COMP fields use binary storage which may be shorter
        if (cobolField.comp()) {
            if (declaredLength <= 4) {
                return 2;  // 16-bit word
            } else if (declaredLength <= 9) {
                return 4;  // 32-bit word
            } else {
                return 8;  // 64-bit word
            }
        } 
        // COMP-3 fields use packed decimal format
        else if (cobolField.comp3()) {
            // Each byte contains 2 digits, plus one nibble for sign
            return (declaredLength / 2) + 1;
        } 
        // Regular fields use one byte per digit/character
        else {
            return declaredLength;
        }
    }
}
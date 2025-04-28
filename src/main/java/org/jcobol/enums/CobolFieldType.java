package org.jcobol.enums;

/**
 * Enumeration of supported COBOL field types.
 * Provides standardized types for the CobolField annotation.
 */
public enum CobolFieldType {
    /**
     * Alphanumeric field (PIC X)
     */
    ALPHANUMERIC("PIC X"),
    
    /**
     * Numeric field (PIC 9) - can be signed using signed() attribute
     */
    NUMERIC("PIC 9"),
    
    /**
     * Decimal field with assumed decimal point (PIC 9(m)V9(n))
     * Scale indicates position of assumed decimal point
     * The length of the field does not include the decimal point
     */
    DECIMAL_ASSUMED("PIC 9(m)V9(n)"),
    
    /**
     * Decimal field with explicit decimal point (PIC 9(m).9(n))
     * Contains an actual decimal point character in the data
     * The length of the field includes the decimal point
     */
    DECIMAL_EXPLICIT("PIC 9(m).9(n)");
    
    private final String cobolNotation;
    
    CobolFieldType(String cobolNotation) {
        this.cobolNotation = cobolNotation;
    }
    
    /**
     * Returns the COBOL notation for this field type
     * 
     * @return The COBOL notation string
     */
    public String getCobolNotation() {
        return cobolNotation;
    }
}
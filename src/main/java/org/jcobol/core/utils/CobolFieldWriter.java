package org.jcobol.core.utils;

import org.jcobol.annotation.CobolField;
import org.jcobol.core.handlers.Comp3FieldHandler;
import org.jcobol.core.handlers.CompFieldHandler;
import org.jcobol.core.handlers.StandardFieldHandler;
import org.jcobol.exception.CobolParseException;

/**
 * Utility class for writing field values to binary data
 */
public class CobolFieldWriter {

    private final CobolFieldCalculator calculator = new CobolFieldCalculator();
    private final CompFieldHandler compHandler = new CompFieldHandler();
    private final Comp3FieldHandler comp3Handler = new Comp3FieldHandler();
    private final StandardFieldHandler standardHandler = new StandardFieldHandler();

    /**
     * Write a field value to a byte array according to its COBOL type.
     *
     * @param value The field value
     * @param data The byte array to write to
     * @param offset The offset position in the byte array
     * @param cobolField The CobolField annotation
     * @return The number of bytes written
     * @throws CobolParseException If there's an error during conversion
     */
    public int writeBinaryFieldValue(Object value, byte[] data, int offset, CobolField cobolField) 
            throws CobolParseException {
        try {
            if (cobolField.comp()) {
                return compHandler.writeValue(value, data, offset, cobolField);
            } else if (cobolField.comp3()) {
                return comp3Handler.writeValue(value, data, offset, cobolField);
            } else {
                return standardHandler.writeValue(value, data, offset, cobolField);
            }
        } catch (Exception e) {
            throw new CobolParseException("Error writing field value: " + e.getMessage(), e);
        }
    }
}
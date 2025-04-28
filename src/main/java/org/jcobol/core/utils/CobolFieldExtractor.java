package org.jcobol.core.utils;

import org.jcobol.annotation.CobolField;
import org.jcobol.core.handlers.Comp3FieldHandler;
import org.jcobol.core.handlers.CompFieldHandler;
import org.jcobol.core.handlers.StandardFieldHandler;
import org.jcobol.enums.CobolFieldType;
import org.jcobol.exception.CobolParseException;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Utility class for extracting field values from binary data
 */
public class CobolFieldExtractor {

    private final CompFieldHandler compHandler = new CompFieldHandler();
    private final Comp3FieldHandler comp3Handler = new Comp3FieldHandler();
    private final StandardFieldHandler standardHandler = new StandardFieldHandler();
    
    /**
     * Extract a field value from binary data according to its COBOL type.
     *
     * @param data The binary data
     * @param startPos Start position in the data
     * @param endPos End position in the data
     * @param field The Java field
     * @param cobolField The CobolField annotation
     * @return The extracted value
     * @throws CobolParseException If the data cannot be parsed correctly
     */
    public Object extractBinaryFieldValue(byte[] data, int startPos, int endPos, 
            Field field, CobolField cobolField) throws CobolParseException {
        byte[] fieldData = Arrays.copyOfRange(data, startPos, endPos);
        CobolFieldType type = cobolField.type();
        Class<?> fieldType = field.getType();
        
        try {
            // Handle different COBOL field types
            if (cobolField.comp()) {
                return compHandler.extractValue(fieldData, fieldType, cobolField);
            } else if (cobolField.comp3()) {
                return comp3Handler.extractValue(fieldData, fieldType, cobolField);
            } else {
                return standardHandler.extractValue(fieldData, fieldType, cobolField);
            }
        } catch (Exception e) {
            throw new CobolParseException("Error extracting field value: " + e.getMessage(), e);
        }
    }
}
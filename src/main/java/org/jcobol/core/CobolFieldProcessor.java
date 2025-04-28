package org.jcobol.core;

import org.jcobol.annotation.CobolField;
import org.jcobol.annotation.CobolNestedObject;
import org.jcobol.core.utils.CobolFieldCalculator;
import org.jcobol.exception.CobolParseException;
import org.jcobol.core.utils.CobolDefaultValueProvider;
import org.jcobol.core.utils.CobolFieldExtractor;
import org.jcobol.core.utils.CobolFieldWriter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade class for working with COBOL-annotated Java objects using binary data.
 * This class delegates to specialized handlers for parsing and writing operations.
 */
public class CobolFieldProcessor {

    private static final CobolFieldCalculator fieldCalculator = new CobolFieldCalculator();
    private static final CobolFieldExtractor fieldExtractor = new CobolFieldExtractor();
    private static final CobolFieldWriter fieldWriter = new CobolFieldWriter();

    /**
     * Parse a byte array record with nested COBOL objects.
     * Extracts values from the byte array and sets them in the provided object.
     *
     * @param obj The object to populate
     * @param data The binary data to parse
     * @param startPos The starting position in the data
     * @return The total length of the parsed record
     * @throws IllegalAccessException If a field cannot be accessed
     * @throws CobolParseException If the record cannot be parsed correctly
     */
    public static int parseFromBinary(Object obj, byte[] data, int startPos)
            throws IllegalAccessException, CobolParseException {
        if (data == null) {
            throw new CobolParseException("Binary data cannot be null");
        }
        
        List<FieldInfo> fieldInfos = new ArrayList<>();
        int currentPos = startPos;
        int totalLength = 0;
        
        // Process all fields including nested objects
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(CobolField.class)) {
                // Handle simple COBOL fields
                CobolField cobolField = field.getAnnotation(CobolField.class);
                FieldInfo info = new FieldInfo(field, cobolField);
                
                int length = fieldCalculator.calculateBinaryLength(cobolField);
                info.setStartPos(currentPos);
                info.setEndPos(currentPos + length);
                currentPos += length;
                totalLength += length;
                
                fieldInfos.add(info);
            } 
            else if (field.isAnnotationPresent(CobolNestedObject.class)) {
                // Handle nested objects
                try {
                    // Create instance of nested object if not already created
                    Object nestedObj = field.get(obj);
                    if (nestedObj == null) {
                        nestedObj = field.getType().getDeclaredConstructor().newInstance();
                        field.set(obj, nestedObj);
                    }
                    
                    // Parse the nested object fields recursively
                    int nestedLength = parseFromBinary(nestedObj, data, currentPos);
                    currentPos += nestedLength;
                    totalLength += nestedLength;
                    
                } catch (InstantiationException | NoSuchMethodException | 
                         InvocationTargetException e) {
                    throw new CobolParseException("Failed to instantiate nested object", e);
                }
            }
            // If no annotation, skip this field
        }
        
        // Parse fields with the gathered position information
        parseFieldsFromBinary(obj, data, fieldInfos);
        
        return totalLength;
    }
    
    /**
     * Parse multiple records from a byte array into a list of objects of the specified class.
     *
     * @param <T> The type of objects to create
     * @param data The byte array containing all records
     * @param clazz The class of the objects to create
     * @return A list of populated objects
     * @throws IllegalAccessException If a field cannot be accessed
     * @throws CobolParseException If a record cannot be parsed correctly
     * @throws ReflectiveOperationException If an instance of the class cannot be created
     */
    public static <T> List<T> parseRecordsFromBinary(byte[] data, Class<T> clazz)
            throws IllegalAccessException, CobolParseException, ReflectiveOperationException {

        if (data == null || data.length == 0) {
            return new ArrayList<>();
        }

        List<T> results = new ArrayList<>();
        int currentPos = 0;
        
        // Continue parsing records until we reach the end of the data
        while (currentPos < data.length) {
            // Create a new instance of the class
            T obj = clazz.getDeclaredConstructor().newInstance();

            // Parse a single record starting at the current position
            int recordLength = parseFromBinary(obj, data, currentPos);

            // Add the parsed object to the results
            results.add(obj);

            // Move to the next record
            currentPos += recordLength;
        }

        return results;
    }
    
    /**
     * Parse fields from a byte array using the provided field position information.
     */
    private static void parseFieldsFromBinary(Object obj, byte[] data, List<FieldInfo> fieldInfos)
            throws IllegalAccessException, CobolParseException {
        for (FieldInfo info : fieldInfos) {
            Field field = info.getField();
            CobolField cobolField = info.getCobolField();
            int startPos = info.getStartPos();
            int endPos = info.getEndPos();
            
            if (startPos >= 0 && endPos <= data.length) {
                field.setAccessible(true);
                
                // Extract and convert field value based on COBOL type
                Object value = fieldExtractor.extractBinaryFieldValue(data, startPos, endPos, field, cobolField);
                field.set(obj, value);
            } else {
                throw new CobolParseException(
                    "Invalid position range for field " + field.getName() + 
                    ": [" + startPos + "," + endPos + "] with data length " + data.length);
            }
        }
    }

    /**
     * Write a COBOL-annotated object to a byte array based on field annotations.
     *
     * @param obj The object to write
     * @return A byte array representation of the object according to COBOL field definitions
     * @throws IllegalAccessException If a field cannot be accessed
     * @throws CobolParseException If there's an error during conversion
     */
    public static byte[] writeToBinary(Object obj) 
            throws IllegalAccessException, CobolParseException {
        // Calculate the total length of the binary output
        int totalLength = calculateObjectBinaryLength(obj);
        byte[] result = new byte[totalLength];
        
        // Fill the byte array with field values
        writeObjectToBinary(obj, result, 0);
        
        return result;
    }
    
    /**
     * Calculate the total binary length of an object including all its fields.
     */
    public static int calculateObjectBinaryLength(Object obj) throws IllegalAccessException {
        int totalLength = 0;
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(CobolField.class)) {
                CobolField cobolField = field.getAnnotation(CobolField.class);
                totalLength += fieldCalculator.calculateBinaryLength(cobolField);
            } 
            else if (field.isAnnotationPresent(CobolNestedObject.class)) {
                Object nestedObj = field.get(obj);
                if (nestedObj != null) {
                    totalLength += calculateObjectBinaryLength(nestedObj);
                }
            }
        }
        
        return totalLength;
    }
    
    /**
     * Recursively write an object and its nested objects to a byte array.
     */
    private static int writeObjectToBinary(Object obj, byte[] data, int offset) 
            throws IllegalAccessException, CobolParseException {
        int currentPos = offset;
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(CobolField.class)) {
                CobolField cobolField = field.getAnnotation(CobolField.class);
                Object value = field.get(obj);
                
                // Convert field value to bytes according to COBOL type
                int bytesWritten = fieldWriter.writeBinaryFieldValue(value, data, currentPos, cobolField);
                currentPos += bytesWritten;
            } 
            else if (field.isAnnotationPresent(CobolNestedObject.class)) {
                // Handle nested objects recursively
                Object nestedObj = field.get(obj);
                if (nestedObj != null) {
                    int bytesWritten = writeObjectToBinary(nestedObj, data, currentPos);
                    currentPos += bytesWritten;
                }
            }
        }
        
        return currentPos - offset;
    }
    
    /**
     * Initialize an object's fields based on COBOL data type annotations.
     * Fields are set to appropriate default values based on their COBOL type.
     *
     * @param obj The object to initialize
     * @throws IllegalAccessException If a field cannot be accessed
     */
    public static void initialize(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(CobolField.class)) {
                CobolField cobolField = field.getAnnotation(CobolField.class);
                field.set(obj, CobolDefaultValueProvider.getDefaultValue(cobolField, field.getType()));
            }
            else if (field.isAnnotationPresent(CobolNestedObject.class)) {
                // Initialize nested objects recursively
                Object nestedObj = field.get(obj);
                
                // Create instance if null
                if (nestedObj == null) {
                    try {
                        nestedObj = field.getType().getDeclaredConstructor().newInstance();
                        field.set(obj, nestedObj);
                    } catch (InstantiationException | NoSuchMethodException | 
                             InvocationTargetException e) {
                        throw new IllegalStateException("Failed to instantiate nested object", e);
                    }
                }
                
                // Recursively initialize this nested object
                initialize(nestedObj);
            }
        }
    }
}
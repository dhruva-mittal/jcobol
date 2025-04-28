package org.jcobol.annotation;

import org.jcobol.enums.CobolFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as a COBOL data field.
 * This annotation provides metadata about how the field should be treated
 * when initializing with COBOL default values or parsing COBOL records.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CobolField {
    /**
     * The COBOL data type of this field
     * 
     * @return The COBOL field type
     */
    CobolFieldType type();
    
    /**
     * The total length of the field in characters
     * 
     * @return The field length
     */
    int length();
    
    /**
     * The number of decimal places (for numeric fields with decimal points)
     * For example, PIC 9(5)V9(2) would have length=7, scale=2
     * 
     * @return The decimal scale
     *          (default 0)
     */
    int scale() default 0;
    
    /**
     * Indicates if the field is a signed numeric field
     * 
     * @return true if the field is signed, false otherwise
     *         (default false)
     */
    boolean signed() default false;

    /**
     * Indicates if the field is a COMP-3 field (packed decimal)
     * 
     * @return true if the field is a COMP-3 field, false otherwise
     *        (default false)
     */
    boolean comp3() default false;

    /**
     * Indicates if the field is a COMP field (binary)
     * 
     * @return true if the field is a COMP field, false otherwise
     *       (default false)
     */
    boolean comp() default false;

    
    /**
     * Optional description of the field (for documentation purposes)
     * 
     * @return Field description
     */
    String description() default "";
}
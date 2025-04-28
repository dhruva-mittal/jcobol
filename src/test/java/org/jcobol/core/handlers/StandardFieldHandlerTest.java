package org.jcobol.core.handlers;

import org.jcobol.annotation.CobolField;
import org.jcobol.enums.CobolFieldType;
import org.jcobol.exception.CobolParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class StandardFieldHandlerTest {

    private StandardFieldHandler handler;
    
    @BeforeEach
    public void setUp() {
        handler = new StandardFieldHandler();
    }
    
    @Test
    public void testExtractAlphanumericValue() throws CobolParseException {
        // Create a mock CobolField annotation
        CobolField cobolField = new CobolField() {
            @Override
            public Class<CobolField> annotationType() {
                return CobolField.class;
            }
            
            @Override
            public CobolFieldType type() {
                return CobolFieldType.ALPHANUMERIC;
            }
            
            @Override
            public int length() {
                return 10;
            }
            
            @Override
            public int scale() {
                return 0;
            }
            
            @Override
            public boolean signed() {
                return false;
            }
            
            @Override
            public boolean comp3() {
                return false;
            }
            
            @Override
            public boolean comp() {
                return false;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Test extracting alphanumeric value
        byte[] testData = "Hello     ".getBytes();
        Object result = handler.extractValue(testData, String.class, cobolField);
        
        assertEquals("Hello     ", result);
    }
    
    @Test
    public void testExtractNumericValue() throws CobolParseException {
        // Create a mock CobolField annotation
        CobolField cobolField = new CobolField() {
            @Override
            public Class<CobolField> annotationType() {
                return CobolField.class;
            }
            
            @Override
            public CobolFieldType type() {
                return CobolFieldType.NUMERIC;
            }
            
            @Override
            public int length() {
                return 5;
            }
            
            @Override
            public int scale() {
                return 0;
            }
            
            @Override
            public boolean signed() {
                return false;
            }
            
            @Override
            public boolean comp3() {
                return false;
            }
            
            @Override
            public boolean comp() {
                return false;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Test extracting numeric value
        byte[] testData = "12345".getBytes();
        Object result = handler.extractValue(testData, Integer.class, cobolField);
        
        assertEquals(12345, result);
    }
    
    @Test
    public void testWriteAlphanumericValue() throws CobolParseException {
        // Create a mock CobolField annotation
        CobolField cobolField = new CobolField() {
            @Override
            public Class<CobolField> annotationType() {
                return CobolField.class;
            }
            
            @Override
            public CobolFieldType type() {
                return CobolFieldType.ALPHANUMERIC;
            }
            
            @Override
            public int length() {
                return 10;
            }
            
            @Override
            public int scale() {
                return 0;
            }
            
            @Override
            public boolean signed() {
                return false;
            }
            
            @Override
            public boolean comp3() {
                return false;
            }
            
            @Override
            public boolean comp() {
                return false;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Test writing alphanumeric value
        byte[] data = new byte[10];
        int bytesWritten = handler.writeValue("Hello", data, 0, cobolField);
        
        assertEquals(10, bytesWritten);
        assertEquals("Hello     ", new String(data));
    }
    
    @Test
    public void testWriteDecimalAssumedValue() throws CobolParseException {
        // Create a mock CobolField annotation
        CobolField cobolField = new CobolField() {
            @Override
            public Class<CobolField> annotationType() {
                return CobolField.class;
            }
            
            @Override
            public CobolFieldType type() {
                return CobolFieldType.DECIMAL_ASSUMED;
            }
            
            @Override
            public int length() {
                return 5;
            }
            
            @Override
            public int scale() {
                return 2;
            }
            
            @Override
            public boolean signed() {
                return false;
            }
            
            @Override
            public boolean comp3() {
                return false;
            }
            
            @Override
            public boolean comp() {
                return false;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Test writing decimal value
        byte[] data = new byte[5];
        int bytesWritten = handler.writeValue(new BigDecimal("123.45"), data, 0, cobolField);
        
        assertEquals(5, bytesWritten);
        assertEquals("12345", new String(data));
    }
}
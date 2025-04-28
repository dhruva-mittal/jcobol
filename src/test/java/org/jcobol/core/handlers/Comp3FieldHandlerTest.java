package org.jcobol.core.handlers;

import org.jcobol.annotation.CobolField;
import org.jcobol.enums.CobolFieldType;
import org.jcobol.exception.CobolParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class Comp3FieldHandlerTest {

    private Comp3FieldHandler handler;
    
    @BeforeEach
    public void setUp() {
        handler = new Comp3FieldHandler();
    }
    
    @Test
    public void testExtractPositiveValue() throws CobolParseException {
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
                return true;
            }
            
            @Override
            public boolean comp3() {
                return true;
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
        
        // Create packed decimal data for 12345 (positive)
        // In COMP-3, this would be 3 bytes: 0x10 | 0x02, 0x30 | 0x04, 0x50 | 0x0C (where C is the sign nibble)
        byte[] testData = new byte[] {0x10 | 0x02, 0x30 | 0x04, 0x50 | 0x0C};
        
        // Test extracting value
        Object result = handler.extractValue(testData, Integer.class, cobolField);
        
        assertEquals(12345, result);
    }
    
    @Test
    public void testExtractNegativeValue() throws CobolParseException {
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
                return true;
            }
            
            @Override
            public boolean comp3() {
                return true;
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
        
        // Create packed decimal data for -12345
        // In COMP-3, this would be 3 bytes: 0x10 | 0x02, 0x30 | 0x04, 0x50 | 0x0D (where D is the sign nibble)
        byte[] testData = new byte[] {0x10 | 0x02, 0x30 | 0x04, 0x50 | 0x0D};
        
        // Test extracting value
        Object result = handler.extractValue(testData, Integer.class, cobolField);
        
        assertEquals(-12345, result);
    }
    
    @Test
    public void testWriteDecimalValue() throws CobolParseException {
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
                return true;
            }
            
            @Override
            public boolean comp3() {
                return true;
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
        byte[] data = new byte[3]; // 5 digits packed into 3 bytes
        BigDecimal value = new BigDecimal("123.45");
        int bytesWritten = handler.writeValue(value, data, 0, cobolField);
        
        // Verify bytes written
        assertEquals(3, bytesWritten);
        
        // Extract back and verify
        Object extractedValue = handler.extractValue(data, BigDecimal.class, cobolField);
        assertEquals(0, value.compareTo((BigDecimal) extractedValue));
    }
    
    @Test
    public void testWriteNegativeValue() throws CobolParseException {
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
                return true;
            }
            
            @Override
            public boolean comp3() {
                return true;
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
        
        // Test writing negative value
        byte[] data = new byte[3]; // 5 digits packed into 3 bytes
        int bytesWritten = handler.writeValue(-12345, data, 0, cobolField);
        
        // Verify bytes written
        assertEquals(3, bytesWritten);
        
        // Verify last nibble is D (negative sign)
        assertEquals(0x0D, data[2] & 0x0F);
        
        // Extract back and verify
        Object extractedValue = handler.extractValue(data, Integer.class, cobolField);
        assertEquals(-12345, extractedValue);
    }
}
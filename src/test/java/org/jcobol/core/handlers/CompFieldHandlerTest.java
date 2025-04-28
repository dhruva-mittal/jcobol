package org.jcobol.core.handlers;

import org.jcobol.annotation.CobolField;
import org.jcobol.enums.CobolFieldType;
import org.jcobol.exception.CobolParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

public class CompFieldHandlerTest {

    private CompFieldHandler handler;
    
    @BeforeEach
    public void setUp() {
        handler = new CompFieldHandler();
    }
    
    @Test
    public void testExtractShortValue() throws CobolParseException {
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
                return 2;
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
                return true;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Create binary data for short value (42)
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short) 42);
        
        // Test extracting value
        Object result = handler.extractValue(buffer.array(), Short.class, cobolField);
        
        assertEquals((short) 42, result);
    }
    
    @Test
    public void testExtractIntValue() throws CobolParseException {
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
                return true;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Create binary data for int value (12345)
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(12345);
        
        // Test extracting value
        Object result = handler.extractValue(buffer.array(), Integer.class, cobolField);
        
        assertEquals(12345, result);
    }
    
    @Test
    public void testWriteIntValue() throws CobolParseException {
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
                return true;
            }
            
            @Override
            public String description() {
                return "";
            }
        };
        
        // Test writing int value
        byte[] data = new byte[4];
        int bytesWritten = handler.writeValue(12345, data, 0, cobolField);
        
        // Verify bytes written
        assertEquals(4, bytesWritten);
        
        // Verify value encoded correctly
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.BIG_ENDIAN);
        assertEquals(12345, buffer.getInt());
    }
}
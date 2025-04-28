package org.jcobol.core.utils;

import org.jcobol.annotation.CobolField;
import org.jcobol.enums.CobolFieldType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CobolFieldCalculatorTest {

    private CobolFieldCalculator calculator;
    
    @BeforeEach
    public void setUp() {
        calculator = new CobolFieldCalculator();
    }
    
    @Test
    public void testCalculateStandardFieldLength() {
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
        
        // Verify standard field length
        assertEquals(10, calculator.calculateBinaryLength(cobolField));
    }
    
    @Test
    public void testCalculateCompFieldLength() {
        // Create mock CobolField annotations for various COMP field lengths
        
        // Small COMP field (1-4 digits)
        CobolField smallComp = new CobolField() {
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
                return 4;
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
        
        // Medium COMP field (5-9 digits)
        CobolField mediumComp = new CobolField() {
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
                return 9;
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
        
        // Large COMP field (10+ digits)
        CobolField largeComp = new CobolField() {
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
                return 15;
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
        
        // Verify binary lengths for different COMP field sizes
        assertEquals(2, calculator.calculateBinaryLength(smallComp)); // 2-byte short
        assertEquals(4, calculator.calculateBinaryLength(mediumComp)); // 4-byte int
        assertEquals(8, calculator.calculateBinaryLength(largeComp)); // 8-byte long
    }
    
    @Test
    public void testCalculateComp3FieldLength() {
        // Create mock CobolField annotations for various COMP-3 field lengths
        
        // Odd number of digits
        CobolField oddComp3 = new CobolField() {
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
        
        // Even number of digits
        CobolField evenComp3 = new CobolField() {
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
                return 6;
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
        
        // Verify binary lengths for COMP-3 fields
        assertEquals(3, calculator.calculateBinaryLength(oddComp3)); // 5 digits + sign = 3 bytes
        assertEquals(4, calculator.calculateBinaryLength(evenComp3)); // 6 digits + sign = 4 bytes
    }
}
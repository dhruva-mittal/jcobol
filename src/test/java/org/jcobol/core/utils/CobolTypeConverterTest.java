package org.jcobol.core.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CobolTypeConverterTest {

    private CobolTypeConverter converter;
    
    @BeforeEach
    public void setUp() {
        converter = new CobolTypeConverter();
    }
    
    @Test
    public void testConvertStringToInteger() {
        Object result = converter.convertToNumericType("123", Integer.class);
        assertEquals(123, result);
    }
    
    @Test
    public void testConvertStringToLong() {
        Object result = converter.convertToNumericType("9876543210", Long.class);
        assertEquals(9876543210L, result);
    }
    
    @Test
    public void testConvertStringToBigDecimal() {
        Object result = converter.convertToNumericType("123.45", BigDecimal.class);
        assertEquals(0, new BigDecimal("123.45").compareTo((BigDecimal) result));
    }
    
    @Test
    public void testConvertNumberToString() {
        Object result = converter.convertToNumericType(123, String.class);
        assertEquals("123", result);
    }
    
    @Test
    public void testConvertNumberToBigDecimal() {
        Object result = converter.convertToNumericType(123, BigDecimal.class);
        assertEquals(0, new BigDecimal("123").compareTo((BigDecimal) result));
    }
    
    @Test
    public void testConvertBigIntegerToOtherNumeric() {
        BigInteger bigInt = new BigInteger("12345");
        
        // Test conversion to various types
        assertEquals(12345, converter.convertToNumericType(bigInt, Integer.class));
        assertEquals(12345L, converter.convertToNumericType(bigInt, Long.class));
        assertEquals(12345.0, converter.convertToNumericType(bigInt, Double.class));
    }
    
    @Test
    public void testConvertNullValue() {
        assertNull(converter.convertToNumericType((Number) null, Integer.class));
        assertEquals("0", converter.convertToNumericType((String) null, String.class));
    }
}
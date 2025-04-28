package org.jcobol.core;

import org.jcobol.annotation.CobolField;
import org.jcobol.annotation.CobolNestedObject;
import org.jcobol.enums.CobolFieldType;
import org.jcobol.exception.CobolParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CobolFieldProcessorTest {

    // Test class representing an employee record
    public static class Employee {
        @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 10)
        private String id;
        
        @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 30)
        private String name;
        
        @CobolField(type = CobolFieldType.NUMERIC, length = 4, comp = true)
        private int age;
        
        @CobolField(type = CobolFieldType.DECIMAL_ASSUMED, length = 7, scale = 2, comp3 = true)
        private BigDecimal salary;
        
        @CobolNestedObject
        private Address address;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        public BigDecimal getSalary() { return salary; }
        public void setSalary(BigDecimal salary) { this.salary = salary; }
        
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }
    
    // Test class representing an address record
    public static class Address {
        @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 30)
        private String street;
        
        @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 20)
        private String city;
        
        @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 2)
        private String state;
        
        @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 5)
        private String zipCode;
        
        // Getters and setters
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }

    @Test
    public void testInitialize() throws IllegalAccessException {
        // Create a new Employee object
        Employee employee = new Employee();
        
        // Initialize with default values
        CobolFieldProcessor.initialize(employee);
        
        // Verify default values were set
        assertNotNull(employee.getId());
        assertEquals("          ", employee.getId()); // 10 spaces
        assertNotNull(employee.getName());
        assertEquals("                              ", employee.getName()); // 30 spaces
        assertEquals(0, employee.getAge());
        assertEquals(new BigDecimal("0"), employee.getSalary());
        
        // Verify nested object was created and initialized
        assertNotNull(employee.getAddress());
        assertNotNull(employee.getAddress().getStreet());
        assertEquals("                              ", employee.getAddress().getStreet()); // 30 spaces
        assertNotNull(employee.getAddress().getCity());
        assertEquals("                    ", employee.getAddress().getCity()); // 20 spaces
        assertNotNull(employee.getAddress().getState());
        assertEquals("  ", employee.getAddress().getState()); // 2 spaces
        assertNotNull(employee.getAddress().getZipCode());
        assertEquals("     ", employee.getAddress().getZipCode()); // 5 spaces
    }

    @Test
    public void testWriteToBinary() throws IllegalAccessException, CobolParseException {
        // Create and populate an Employee object
        Employee employee = new Employee();
        employee.setId("EMP001");
        employee.setName("John Doe");
        employee.setAge(42);
        employee.setSalary(new BigDecimal("65000.00"));
        
        // Create and set Address
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62701");
        employee.setAddress(address);
        
        // Convert to binary
        byte[] binaryData = CobolFieldProcessor.writeToBinary(employee);
        
        // Test that binary data was created
        assertNotNull(binaryData);
        
        // Verify the length matches expected
        int expectedLength = 10 + 30 + 2 + 4 + 30 + 20 + 2 + 5; // All fields plus COMP/COMP3 adjustments
        assertEquals(expectedLength, binaryData.length);
        
        // Create a new object to parse the binary data
        Employee parsedEmployee = new Employee();
        CobolFieldProcessor.parseFromBinary(parsedEmployee, binaryData, 0);
        
        // Verify the parsed object matches the original
        assertEquals("EMP001", parsedEmployee.getId().trim());
        assertEquals("John Doe", parsedEmployee.getName().trim());
        assertEquals(42, parsedEmployee.getAge());
        assertEquals(0, new BigDecimal("65000.00").compareTo(parsedEmployee.getSalary()));
        
        // Verify nested object was parsed correctly
        assertNotNull(parsedEmployee.getAddress());
        assertEquals("123 Main St", parsedEmployee.getAddress().getStreet().trim());
        assertEquals("Springfield", parsedEmployee.getAddress().getCity().trim());
        assertEquals("IL", parsedEmployee.getAddress().getState().trim());
        assertEquals("62701", parsedEmployee.getAddress().getZipCode().trim());
    }

    @Test
    public void testParseMultipleRecords() throws Exception {
        // Create sample employee records
        Employee emp1 = new Employee();
        emp1.setId("EMP001");
        emp1.setName("John Doe");
        emp1.setAge(42);
        emp1.setSalary(new BigDecimal("65000.00"));
        
        Address addr1 = new Address();
        addr1.setStreet("123 Main St");
        addr1.setCity("Springfield");
        addr1.setState("IL");
        addr1.setZipCode("62701");
        emp1.setAddress(addr1);
        
        Employee emp2 = new Employee();
        emp2.setId("EMP002");
        emp2.setName("Jane Smith");
        emp2.setAge(35);
        emp2.setSalary(new BigDecimal("72500.50"));
        
        Address addr2 = new Address();
        addr2.setStreet("456 Oak Ave");
        addr2.setCity("Chicago");
        addr2.setState("IL");
        addr2.setZipCode("60601");
        emp2.setAddress(addr2);
        
        // Convert both to binary
        byte[] binaryEmp1 = CobolFieldProcessor.writeToBinary(emp1);
        byte[] binaryEmp2 = CobolFieldProcessor.writeToBinary(emp2);
        
        // Combine the binary data
        byte[] combinedData = new byte[binaryEmp1.length + binaryEmp2.length];
        System.arraycopy(binaryEmp1, 0, combinedData, 0, binaryEmp1.length);
        System.arraycopy(binaryEmp2, 0, combinedData, binaryEmp1.length, binaryEmp2.length);
        
        // Parse multiple records
        List<Employee> employees = CobolFieldProcessor.parseRecordsFromBinary(combinedData, Employee.class);
        
        // Verify two records were parsed
        assertEquals(2, employees.size());
        
        // Verify first employee
        Employee parsed1 = employees.get(0);
        assertEquals("EMP001", parsed1.getId().trim());
        assertEquals("John Doe", parsed1.getName().trim());
        assertEquals(42, parsed1.getAge());
        assertEquals(0, new BigDecimal("65000.00").compareTo(parsed1.getSalary()));
        assertEquals("123 Main St", parsed1.getAddress().getStreet().trim());
        
        // Verify second employee
        Employee parsed2 = employees.get(1);
        assertEquals("EMP002", parsed2.getId().trim());
        assertEquals("Jane Smith", parsed2.getName().trim());
        assertEquals(35, parsed2.getAge());
        assertEquals(0, new BigDecimal("72500.50").compareTo(parsed2.getSalary()));
        assertEquals("456 Oak Ave", parsed2.getAddress().getStreet().trim());
    }

    @Test
    public void testParseNullData() {
        // Test that parsing null data throws appropriate exception
        Employee employee = new Employee();
        Exception exception = assertThrows(CobolParseException.class, () -> {
            CobolFieldProcessor.parseFromBinary(employee, null, 0);
        });
        
        assertTrue(exception.getMessage().contains("Binary data cannot be null"));
    }
    
    @Test
    public void testCalculateObjectBinaryLength() throws IllegalAccessException {
        // Create an Employee object
        Employee employee = new Employee();
        employee.setId("EMP001");
        employee.setName("John Doe");
        employee.setAge(42);
        employee.setSalary(new BigDecimal("65000.00"));
        
        // Create and set Address
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Springfield");
        address.setState("IL");
        address.setZipCode("62701");
        employee.setAddress(address);
        
        // Calculate binary length
        int calculatedLength = CobolFieldProcessor.calculateObjectBinaryLength(employee);
        
        // Calculate expected length manually
        int expectedLength = 10 + 30 + 2 + 4 + 30 + 20 + 2 + 5; // All fields plus COMP/COMP3 adjustments
        
        // Verify length calculation is correct
        assertEquals(expectedLength, calculatedLength);
    }
}
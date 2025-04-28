# JCobol - Java COBOL Data Processor

A lightweight Java library for processing COBOL data structures without the complexity.

## Overview

JCobol lets you map Java objects to COBOL data structures using simple annotations. It handles:

- Reading binary COBOL records into Java objects
- Writing Java objects to COBOL-compatible binary formats
- Supporting COMP (binary), COMP-3 (packed decimal), and standard COBOL fields
- Managing nested data structures

## Installation
### Using JitPack

Just add JitPack to your repositories and pull in this library.

#### Step 1: Add JitPack to your build

**Maven:**

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

**Gradle:**

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

#### Step 2: Add the dependency

Replace `TAG` with the release/tag/commit you want (e.g. `main-SNAPSHOT` or a release tag):

**Maven:**

```xml

<dependency>
    <groupId>com.github.dhruva-mittal</groupId>
    <artifactId>jcobol</artifactId>
    <version>TAG</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'com.github.dhruva-mittal:jcobol:TAG'
```

> **Tip:** For the latest code, use `main-SNAPSHOT` as the version. For a specific release, use the tag name.


## Quick Start

```java
// Define your COBOL record structure
public class Employee {
    @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 10)
    private String id;
    
    @CobolField(type = CobolFieldType.ALPHANUMERIC, length = 30)
    private String name;
    
    @CobolField(type = CobolFieldType.NUMERIC, length = 4, comp = true)
    private int age;
    
    @CobolField(type = CobolFieldType.DECIMAL_ASSUMED, length = 7, scale = 2, comp3 = true)
    private BigDecimal salary;
}
```

## Reading COBOL Data

```java
// Parse a binary record
byte[] data = Files.readAllBytes(Paths.get("employees.dat"));
Employee employee = new Employee();
CobolFieldProcessor.parseFromBinary(employee, data, 0);

// Process multiple records
List<Employee> employees = CobolFieldProcessor.parseRecordsFromBinary(data, Employee.class);
```

## Writing COBOL Data

```java
// Create a record
Employee employee = new Employee();
employee.setId("EMP001");
employee.setName("John Doe");
employee.setAge(42);
employee.setSalary(new BigDecimal("65000.00"));

// Convert to binary format
byte[] binaryData = CobolFieldProcessor.writeToBinary(employee);
```

## Supported Field Types

- `ALPHANUMERIC`: Text fields (PIC X)
- `NUMERIC`: Text fields (PIC X)
- `DECIMAL_ASSUMED`: Decimal values with implied decimal point (PIC 9(m)V9(n))
- `DECIMAL_EXPLICIT`: Decimal values with actual decimal point (PIC 9(m).9(n))


## Field Attributes
- `length`: Total field length in characters/digits
- `scale`: Number of decimal places (for decimal types)
- `signed`: Whether the field has a sign
- `comp`: Binary format (COMP)
- `comp3`: Packed decimal format (COMP-3)


## Wanna Help?

Contributions are always welcome! Check out [CONTRIBUTING.md](CONTRIBUTING.md) for the vibe and how to get started.


## License
MIT [LICENSE](LICENSE) - use it however you want!
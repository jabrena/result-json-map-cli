# Field-Value Pairs CLI

A command-line tool built with picocli that accepts field-value pairs and returns them in a JSON structure wrapped in `<result>` tags.

## Output Format

The CLI outputs field-value pairs in the following format:
```xml
<result>{"field": "value", "field2": "value2", ...}</result>
```

## Requirements

- Java 21 or higher
- Maven 3.6+

## Building the Project

```bash
mvn clean package
```

This will create an executable JAR file in the `target` directory.

## Usage

### Basic Usage

```bash
java -jar target/field-value-pairs-cli-1.0.0.jar field=value field2=value2
```

Output:
```xml
<result>{"field":"value","field2":"value2"}</result>
```

### Using Colon Delimiter

The CLI also supports colon (`:`) as a delimiter:

```bash
java -jar target/field-value-pairs-cli-1.0.0.jar name:John age:30 city:New York
```

Output:
```xml
<result>{"name":"John","age":"30","city":"New York"}</result>
```

### Mixed Delimiters

You can mix both `=` and `:` delimiters:

```bash
java -jar target/field-value-pairs-cli-1.0.0.jar name=John age:30 city=New York
```

### Values with Spaces

Values containing spaces are supported:

```bash
java -jar target/field-value-pairs-cli-1.0.0.jar name="John Doe" email=john@example.com
```

### Help and Version

```bash
# Show help
java -jar target/field-value-pairs-cli-1.0.0.jar --help

# Show version
java -jar target/field-value-pairs-cli-1.0.0.jar --version
```

## Running Tests

```bash
mvn test
```

## Examples

### Example 1: Simple field-value pairs
```bash
java -jar target/field-value-pairs-cli-1.0.0.jar name=Alice age=25
```
Output:
```xml
<result>{"name":"Alice","age":"25"}</result>
```

### Example 2: Multiple pairs with special characters
```bash
java -jar target/field-value-pairs-cli-1.0.0.jar email=user@example.com path=/home/user/file.txt
```
Output:
```xml
<result>{"email":"user@example.com","path":"/home/user/file.txt"}</result>
```

### Example 3: Using colon delimiter
```bash
java -jar target/field-value-pairs-cli-1.0.0.jar name:Bob city:San Francisco
```
Output:
```xml
<result>{"name":"Bob","city":"San Francisco"}</result>
```

## Error Handling

The CLI validates input and provides error messages for:
- Invalid pair format (missing delimiter)
- Empty field names
- Missing required arguments

## Project Structure

```
.
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── cli/
    │                   └── FieldValuePairsCli.java
    └── test/
        └── java/
            └── com/
                └── example/
                    └── cli/
                        └── FieldValuePairsCliTest.java
```

## Technologies Used

- **Java 21**: Programming language
- **Maven**: Build tool and dependency management
- **Picocli 4.7.5**: CLI framework
- **Gson 2.10.1**: JSON processing
- **JUnit 5**: Testing framework

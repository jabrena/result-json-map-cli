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

This will create an executable JAR file named `result-json-map-cli-1.0.0.jar` in the `target` directory.

## Usage

The CLI supports two modes of operation:

### Mode 1: Key-Value Pairs with Delimiters

Use `=` or `:` to separate field names and values:

```bash
java -jar target/result-json-map-cli-1.0.0.jar field=value field2=value2
```

Output:
```xml
<result>{"field":"value","field2":"value2"}</result>
```

### Mode 2: Build Command with Alternating Arguments

Use the `build` subcommand with alternating field names and values:

```bash
java -jar target/result-json-map-cli-1.0.0.jar build "field" 30 "field2" "John"
```

Output:
```xml
<result>{"field":30,"field2":"John"}</result>
```

Notice that numeric values (like `30`) are output without quotes, while string values (like `"John"`) have quotes.

### Using Colon Delimiter

The CLI also supports colon (`:`) as a delimiter:

```bash
java -jar target/result-json-map-cli-1.0.0.jar name:John age:30 city:New York
```

Output:
```xml
<result>{"name":"John","age":30,"city":"New York"}</result>
```

Notice that `age:30` produces a numeric value (without quotes) because 30 is a number.

### Mixed Delimiters

You can mix both `=` and `:` delimiters:

```bash
java -jar target/result-json-map-cli-1.0.0.jar name=John age:30 city=New York
```

### Values with Spaces

Values containing spaces are supported:

```bash
java -jar target/result-json-map-cli-1.0.0.jar name="John Doe" email=john@example.com
```

### Numeric Values

Numeric values (integers and decimals) are automatically detected and output without quotes. If you want a numeric value to be treated as a string, wrap it in quotes:

```bash
java -jar target/result-json-map-cli-1.0.0.jar age=30 price=19.99 code="123"
```

Output:
```xml
<result>{"age":30,"price":19.99,"code":"123"}</result>
```

Notice that `age` and `price` are numeric (without quotes), while `code` is a string (with quotes) because it was quoted in the input.

### Help and Version

```bash
# Show help
java -jar target/result-json-map-cli-1.0.0.jar --help

# Show version
java -jar target/result-json-map-cli-1.0.0.jar --version
```

## Running Tests

```bash
mvn test
```

## Examples

### Example 1: Simple field-value pairs
```bash
java -jar target/result-json-map-cli-1.0.0.jar name=Alice age=25
```
Output:
```xml
<result>{"name":"Alice","age":"25"}</result>
```

### Example 2: Multiple pairs with special characters
```bash
java -jar target/result-json-map-cli-1.0.0.jar email=user@example.com path=/home/user/file.txt
```
Output:
```xml
<result>{"email":"user@example.com","path":"/home/user/file.txt"}</result>
```

### Example 3: Using colon delimiter
```bash
java -jar target/result-json-map-cli-1.0.0.jar name:Bob city:San Francisco
```
Output:
```xml
<result>{"name":"Bob","city":"San Francisco"}</result>
```

### Example 4: Numeric values
```bash
java -jar target/result-json-map-cli-1.0.0.jar name=Alice age=25 price=19.99 balance=-100.50
```
Output:
```xml
<result>{"name":"Alice","age":25,"price":19.99,"balance":-100.5}</result>
```

Notice that numeric values (`age`, `price`, `balance`) are output without quotes, while string values (`name`) have quotes.

### Example 5: Using build command
```bash
java -jar target/result-json-map-cli-1.0.0.jar build "name" "Alice" "age" 25 "price" 19.99
```
Output:
```xml
<result>{"name":"Alice","age":25,"price":19.99}</result>
```

Field names can be quoted or unquoted. Values are parsed as numbers if they're numeric and unquoted.

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
    │                   └── ResultGeneratorCli.java
    └── test/
        └── java/
            └── com/
                └── example/
                    └── cli/
                        └── ResultGeneratorCliTest.java
```

## Technologies Used

- **Java 21**: Programming language
- **Maven**: Build tool and dependency management
- **Picocli 4.7.5**: CLI framework
- **Jackson 2.16.1**: JSON processing
- **JUnit 5**: Testing framework

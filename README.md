# Field-Value Pairs CLI

A command-line tool built with picocli that accepts field-value pairs and returns them in a JSON structure wrapped in `<result>` tags.

## Output Format

The CLI outputs field-value pairs in the following format:
```xml
<result>{"field": "value", "field2": "value2", ...}</result>
```

## Building the Project

```bash
mvn clean package
```

This will create an executable JAR file named `result-json-map-cli-0.1.0-SNAPSHOT.jar` in the `target` directory.

## Usage

The CLI supports two modes of operation:

### Mode 1: Key-Value Pairs with Delimiters

Use `=` or `:` to separate field names and values:

```bash
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --help
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --version
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar field=value field2=value2
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar field:value field2:value2
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar build "field" 30 "field2" "John"
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar build name="John Doe" email="john@example.com"
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar age=30 price=19.99 code="123"
```

# Result to JSON map CLI

A CLI tool designed to help models when they return data in a structured way.

```bash
<result>{“field”: “value”, “field2”: “value2”,…}</result>
```

## Hot to build?

```bash
./mvnw clean package
```

## Usage from the build

```bash
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --help
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build field=value field2=value2
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build field:value field2:value2
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build "field" 30 "field2" "John"
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build name="John Doe" email="john@example.com"
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build age=30 price=19.99 code="123"
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build name=
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build name
java -jar target/result-json-map-0.1.0-SNAPSHOT.jar --build
```

## Usage from Jbang

```bash
sdk install jbang

jbang cache clear
jbang catalog list jabrena
jbang trust add https://github.com/jabrena/

jbang result-json-map.0.1.0-SNAPSHOT@jabrena --build name="John Doe" email="john@example.com"
```

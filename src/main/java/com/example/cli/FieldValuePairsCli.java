package com.example.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Command(
    name = "field-value-cli",
    description = "CLI tool that accepts field-value pairs and returns JSON structure",
    mixinStandardHelpOptions = true,
    version = "1.0.0"
)
public class FieldValuePairsCli implements Callable<Integer> {

    @Parameters(
        index = "0..*",
        description = "Field-value pairs in the format 'field=value' or 'field:value'",
        arity = "1..*"
    )
    private String[] pairs;

    @Option(
        names = {"-d", "--delimiter"},
        description = "Delimiter used to separate field and value (default: '=' or ':')",
        defaultValue = "="
    )
    private String delimiter;

    @Override
    public Integer call() {
        try {
            Map<String, Object> fieldValueMap = parsePairs();
            String jsonOutput = convertToJson(fieldValueMap);
            String result = wrapInResultTag(jsonOutput);
            System.out.println(result);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private Map<String, Object> parsePairs() {
        Map<String, Object> map = new HashMap<>();
        
        for (String pair : pairs) {
            String[] parts;
            
            // Try both '=' and ':' as delimiters
            if (pair.contains("=")) {
                parts = pair.split("=", 2);
            } else if (pair.contains(":")) {
                parts = pair.split(":", 2);
            } else {
                throw new IllegalArgumentException(
                    "Invalid pair format: '" + pair + "'. Expected format: 'field=value' or 'field:value'"
                );
            }
            
            if (parts.length != 2) {
                throw new IllegalArgumentException(
                    "Invalid pair format: '" + pair + "'. Expected format: 'field=value' or 'field:value'"
                );
            }
            
            String field = parts[0].trim();
            String value = parts[1].trim();
            
            if (field.isEmpty()) {
                throw new IllegalArgumentException("Field name cannot be empty in pair: '" + pair + "'");
            }
            
            Object parsedValue = parseValue(value);
            map.put(field, parsedValue);
        }
        
        return map;
    }

    private Object parseValue(String value) {
        // Check if value is quoted (starts and ends with quotes)
        if ((value.startsWith("\"") && value.endsWith("\"")) || 
            (value.startsWith("'") && value.endsWith("'"))) {
            // Remove quotes and treat as string
            return value.substring(1, value.length() - 1);
        }
        
        // Try to parse as integer
        try {
            // Check if it's a valid integer (no decimal point)
            if (!value.contains(".") && !value.contains("e") && !value.contains("E")) {
                long longValue = Long.parseLong(value);
                // If it fits in int range, return Integer, otherwise Long
                if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                    return (int) longValue;
                }
                return longValue;
            }
        } catch (NumberFormatException e) {
            // Not an integer, continue to try double
        }
        
        // Try to parse as double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            // Not a number, treat as string
            return value;
        }
    }

    private String convertToJson(Map<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    private String wrapInResultTag(String json) {
        return "<result>" + json + "</result>";
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FieldValuePairsCli()).execute(args);
        System.exit(exitCode);
    }
}

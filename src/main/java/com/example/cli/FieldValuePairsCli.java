package com.example.cli;

import com.google.gson.Gson;
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
            Map<String, String> fieldValueMap = parsePairs();
            String jsonOutput = convertToJson(fieldValueMap);
            String result = wrapInResultTag(jsonOutput);
            System.out.println(result);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private Map<String, String> parsePairs() {
        Map<String, String> map = new HashMap<>();
        
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
            
            map.put(field, value);
        }
        
        return map;
    }

    private String convertToJson(Map<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    private String wrapInResultTag(String json) {
        return "<result>" + json + "</result>";
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new FieldValuePairsCli()).execute(args);
        System.exit(exitCode);
    }
}

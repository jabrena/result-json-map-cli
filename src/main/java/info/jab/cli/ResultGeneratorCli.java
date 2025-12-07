package info.jab.cli;

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
    version = "0.1.0-SNAPSHOT",
    subcommands = {BuildCommand.class}
)
public class ResultGeneratorCli implements Callable<Integer> {

    @Parameters(
        index = "0..*",
        description = "Field-value pairs in the format 'field=value' or 'field:value'",
        arity = "0..*"
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
            if (pairs == null || pairs.length == 0) {
                CommandLine.usage(this, System.out);
                return 0;
            }
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
        
        if (pairs == null || pairs.length == 0) {
            return map;
        }
        
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

    protected Object parseValue(String value) {
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

    protected String convertToJson(Map<String, Object> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    protected String wrapInResultTag(String json) {
        return "<result>" + json + "</result>";
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ResultGeneratorCli()).execute(args);
        System.exit(exitCode);
    }
}

@Command(
    name = "build",
    description = "Build JSON structure from alternating field names and values"
)
class BuildCommand implements Callable<Integer> {

    @Parameters(arity = "0..*")
    private String[] args = new String[0];

    @Override
    public Integer call() {
        try {
            if (args == null || args.length < 2) {
                throw new IllegalArgumentException(
                    "Invalid number of arguments. Expected at least 2 arguments (field-value pairs)."
                );
            }
            if (args.length % 2 != 0) {
                throw new IllegalArgumentException(
                    "Invalid number of arguments. Expected even number of arguments (field-value pairs)."
                );
            }

            ResultGeneratorCli parent = new ResultGeneratorCli();
            Map<String, Object> fieldValueMap = parseAlternatingPairs(args, parent);
            String jsonOutput = parent.convertToJson(fieldValueMap);
            String result = parent.wrapInResultTag(jsonOutput);
            System.out.println(result);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private Map<String, Object> parseAlternatingPairs(String[] args, ResultGeneratorCli parent) {
        Map<String, Object> map = new HashMap<>();
        
        for (int i = 0; i < args.length; i += 2) {
            String field = removeQuotes(args[i].trim());
            String value = args[i + 1].trim();
            
            if (field.isEmpty()) {
                throw new IllegalArgumentException("Field name cannot be empty at position " + (i + 1));
            }
            
            Object parsedValue = parent.parseValue(value);
            map.put(field, parsedValue);
        }
        
        return map;
    }

    private String removeQuotes(String str) {
        if ((str.startsWith("\"") && str.endsWith("\"")) || 
            (str.startsWith("'") && str.endsWith("'"))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
}

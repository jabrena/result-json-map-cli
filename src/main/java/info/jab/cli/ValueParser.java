package info.jab.cli;

/**
 * Utility class for parsing string values to appropriate Java types.
 * Supports quoted strings, integers, longs, doubles, and plain strings.
 */
public class ValueParser {

    /**
     * Parses a string value to the appropriate Java type.
     * - Quoted strings (single or double quotes) are returned as unquoted strings
     * - Numeric strings are parsed as Integer, Long, or Double
     * - Other strings are returned as-is
     *
     * @param value the string value to parse
     * @return the parsed value as Object (String, Integer, Long, or Double)
     */
    public Object parseValue(String value) {
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

    /**
     * Removes surrounding quotes (single or double) from a string if present.
     *
     * @param str the string to process
     * @return the string without surrounding quotes
     */
    public String removeQuotes(String str) {
        if ((str.startsWith("\"") && str.endsWith("\"")) ||
            (str.startsWith("'") && str.endsWith("'"))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
}

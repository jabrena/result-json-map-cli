package info.jab.cli;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing field-value pairs from different input formats.
 * Supports:
 * - Delimited pairs: "field=value" or "field:value"
 * - Alternating pairs: "field" "value" "field2" "value2"
 */
public class PairParser {

    private final ValueParser valueParser;

    public PairParser() {
        this.valueParser = new ValueParser();
    }

    public PairParser(ValueParser valueParser) {
        this.valueParser = valueParser;
    }

    /**
     * Parses field-value pairs from delimited format (field=value or field:value).
     *
     * @param pairs array of strings in format "field=value" or "field:value"
     * @return map of field names to parsed values
     * @throws IllegalArgumentException if pair format is invalid
     */
    public Map<String, Object> parseDelimitedPairs(String[] pairs) {
        Map<String, Object> map = new HashMap<>();

        if (pairs == null || pairs.length == 0) {
            return map;
        }

        for (String pair : pairs) {
            String[] parts = splitPair(pair);

            String field = parts[0].trim();
            String value = parts[1].trim();

            if (field.isEmpty()) {
                throw new IllegalArgumentException("Field name cannot be empty in pair: '" + pair + "'");
            }

            Object parsedValue = valueParser.parseValue(value);
            map.put(field, parsedValue);
        }

        return map;
    }

    /**
     * Parses field-value pairs from alternating format (field, value, field2, value2, ...).
     *
     * @param pairs array of strings where even indices are field names and odd indices are values
     * @return map of field names to parsed values
     * @throws IllegalArgumentException if number of arguments is invalid or field name is empty
     */
    public Map<String, Object> parseAlternatingPairs(String[] pairs) {
        if (pairs == null || pairs.length < 2) {
            throw new IllegalArgumentException(
                "Invalid number of arguments. Expected at least 2 arguments (field-value pairs)."
            );
        }
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Invalid number of arguments. Expected even number of arguments (field-value pairs)."
            );
        }

        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < pairs.length; i += 2) {
            String field = valueParser.removeQuotes(pairs[i].trim());
            String value = pairs[i + 1].trim();

            if (field.isEmpty()) {
                throw new IllegalArgumentException("Field name cannot be empty at position " + (i + 1));
            }

            Object parsedValue = valueParser.parseValue(value);
            map.put(field, parsedValue);
        }

        return map;
    }

    /**
     * Checks if the given pairs contain delimiter characters (= or :).
     *
     * @param pairs array of strings to check
     * @return true if any pair contains = or :, false otherwise
     */
    public boolean containsDelimiters(String[] pairs) {
        if (pairs == null || pairs.length == 0) {
            return false;
        }
        for (String pair : pairs) {
            if (pair.contains("=") || pair.contains(":")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Splits a pair string by delimiter (= or :).
     *
     * @param pair the pair string to split
     * @return array with two elements: [field, value]
     * @throws IllegalArgumentException if pair format is invalid
     */
    private String[] splitPair(String pair) {
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

        return parts;
    }
}

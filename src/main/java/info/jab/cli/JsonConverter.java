package info.jab.cli;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Utility class for converting maps to JSON and wrapping in result tags.
 */
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper();
    }

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Converts a map to JSON string.
     *
     * @param map the map to convert
     * @return JSON string representation
     * @throws Exception if conversion fails
     */
    public String convertToJson(Map<String, Object> map) throws Exception {
        return objectMapper.writeValueAsString(map);
    }

    /**
     * Wraps a JSON string in result tags.
     *
     * @param json the JSON string to wrap
     * @return the wrapped result: &lt;result&gt;{json}&lt;/result&gt;
     */
    public String wrapInResultTag(String json) {
        return "<result>" + json + "</result>";
    }

    /**
     * Converts a map to JSON and wraps it in result tags.
     *
     * @param map the map to convert
     * @return the wrapped JSON result
     * @throws Exception if conversion fails
     */
    public String convertAndWrap(Map<String, Object> map) throws Exception {
        String json = convertToJson(map);
        return wrapInResultTag(json);
    }
}


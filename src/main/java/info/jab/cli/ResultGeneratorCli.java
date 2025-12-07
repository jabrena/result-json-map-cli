package info.jab.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Map;
import java.util.concurrent.Callable;

@Command(
    name = "result-json-map",
    description = "CLI tool that accepts field-value pairs and returns JSON structure",
    version = "0.1.0-SNAPSHOT",
    mixinStandardHelpOptions = true)
public class ResultGeneratorCli implements Callable<Integer> {

    @Option(
        names = {"-d", "--delimiter"},
        description = "Delimiter used to separate field and value (default: '=' or ':')",
        defaultValue = "="
    )
    private String delimiter;

    @Option(
        names = {"-b", "--build"},
        description = "Build JSON structure from alternating field names and values or delimited pairs (field=value or field:value)",
        arity = "1..*"
    )
    private String[] buildPairs;

    private final PairParser pairParser;
    private final JsonConverter jsonConverter;

    public ResultGeneratorCli() {
        this.pairParser = new PairParser();
        this.jsonConverter = new JsonConverter();
    }

    // Constructor for testing purposes
    ResultGeneratorCli(PairParser pairParser, JsonConverter jsonConverter) {
        this.pairParser = pairParser;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public Integer call() {
        try {
            if (buildPairs == null || buildPairs.length == 0) {
                CommandLine.usage(this, System.out);
                return 0;
            }

            Map<String, Object> fieldValueMap;
            // Check if arguments contain = or : delimiters
            // If yes, parse as field=value pairs; otherwise, parse as alternating pairs
            if (pairParser.containsDelimiters(buildPairs)) {
                fieldValueMap = pairParser.parseDelimitedPairs(buildPairs);
            } else {
                fieldValueMap = pairParser.parseAlternatingPairs(buildPairs);
            }

            String result = jsonConverter.convertAndWrap(fieldValueMap);
            System.out.println(result);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ResultGeneratorCli()).execute(args);
        System.exit(exitCode);
    }
}

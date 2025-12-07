package info.jab.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class ResultGeneratorCliTest {

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testSingleFieldValuePair() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name=John");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.startsWith("<result>"));
        assertTrue(output.endsWith("</result>"));
        assertTrue(output.contains("\"name\":\"John\""));
    }

    @Test
    void testMultipleFieldValuePairs() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("field=value", "field2=value2", "field3=value3");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.startsWith("<result>"));
        assertTrue(output.endsWith("</result>"));
        assertTrue(output.contains("\"field\":\"value\""));
        assertTrue(output.contains("\"field2\":\"value2\""));
        assertTrue(output.contains("\"field3\":\"value3\""));
    }

    @Test
    void testColonDelimiter() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name:John", "age:30");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"John\""));
        // Age should be numeric (without quotes)
        assertTrue(output.contains("\"age\":30"));
        assertFalse(output.contains("\"age\":\"30\""));
    }

    @Test
    void testMixedDelimiters() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name=John", "age:30", "city=New York");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"John\""));
        // Age should be numeric (without quotes)
        assertTrue(output.contains("\"age\":30"));
        assertFalse(output.contains("\"age\":\"30\""));
        assertTrue(output.contains("\"city\":\"New York\""));
    }

    @Test
    void testValueWithSpaces() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name=John Doe", "city=New York");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"John Doe\""));
        assertTrue(output.contains("\"city\":\"New York\""));
    }

    @Test
    void testValueWithSpecialCharacters() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("email=user@example.com", "path=/home/user/file.txt");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"email\":\"user@example.com\""));
        assertTrue(output.contains("\"path\":\"/home/user/file.txt\""));
    }

    @Test
    void testEmptyValue() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("field=");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"field\":\"\""));
    }

    @Test
    void testInvalidPairFormat() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("invalidpair");
        
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error"));
        assertTrue(errorOutput.contains("Invalid pair format"));
    }

    @Test
    void testEmptyFieldName() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("=value");
        
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error"));
        assertTrue(errorOutput.contains("Field name cannot be empty"));
    }

    @Test
    void testHelpOption() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("--help");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Usage:"));
        assertTrue(output.contains("field-value-cli"));
    }

    @Test
    void testVersionOption() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("--version");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("0.1.0-SNAPSHOT"));
    }

    @Test
    void testNoArguments() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute();
        
        // Should show help or error
        assertTrue(exitCode != 0 || outContent.toString().contains("Usage:"));
    }

    @Test
    void testJsonStructure() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("field=value", "field2=value2");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        
        // Verify the exact structure: <result>{"field":"value","field2":"value2"}</result>
        assertTrue(output.matches("<result>\\{.*\\}</result>"));
        
        // Verify JSON is valid
        String jsonPart = output.substring(8, output.length() - 9); // Remove <result> and </result>
        assertTrue(jsonPart.startsWith("{"));
        assertTrue(jsonPart.endsWith("}"));
        assertTrue(jsonPart.contains("\"field\""));
        assertTrue(jsonPart.contains("\"field2\""));
    }

    @Test
    void testWhitespaceTrimming() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("  field  =  value  ", "  field2  :  value2  ");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"field\":\"value\""));
        assertTrue(output.contains("\"field2\":\"value2\""));
    }

    @Test
    void testNumericIntegerValue() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("age=25", "count=100");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Numeric values should not have quotes
        assertTrue(output.contains("\"age\":25"));
        assertFalse(output.contains("\"age\":\"25\""));
        assertTrue(output.contains("\"count\":100"));
        assertFalse(output.contains("\"count\":\"100\""));
    }

    @Test
    void testNumericDecimalValue() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("price=19.99", "temperature=-5.5");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Decimal values should not have quotes
        assertTrue(output.contains("\"price\":19.99"));
        assertFalse(output.contains("\"price\":\"19.99\""));
        assertTrue(output.contains("\"temperature\":-5.5"));
    }

    @Test
    void testQuotedNumericValue() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("code=\"123\"", "id='456'");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Quoted numeric values should remain as strings
        assertTrue(output.contains("\"code\":\"123\""));
        assertTrue(output.contains("\"id\":\"456\""));
    }

    @Test
    void testMixedNumericAndStringValues() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name=John", "age=30", "price=19.99", "city=New York");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // String values should have quotes
        assertTrue(output.contains("\"name\":\"John\""));
        assertTrue(output.contains("\"city\":\"New York\""));
        // Numeric values should not have quotes
        assertTrue(output.contains("\"age\":30"));
        assertFalse(output.contains("\"age\":\"30\""));
        assertTrue(output.contains("\"price\":19.99"));
        assertFalse(output.contains("\"price\":\"19.99\""));
    }

    @Test
    void testNegativeNumbers() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("temperature=-10", "balance=-123.45");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Negative numbers should not have quotes
        assertTrue(output.contains("\"temperature\":-10"));
        assertTrue(output.contains("\"balance\":-123.45"));
    }

    @Test
    void testLargeInteger() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("small=100", "large=3000000000");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Both should be numeric (without quotes)
        assertTrue(output.contains("\"small\":100"));
        assertTrue(output.contains("\"large\":3000000000"));
        assertFalse(output.contains("\"small\":\"100\""));
        assertFalse(output.contains("\"large\":\"3000000000\""));
    }

    @Test
    void testBuildCommandBasic() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "field", "value", "field2", "value2");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.startsWith("<result>"));
        assertTrue(output.endsWith("</result>"));
        assertTrue(output.contains("\"field\":\"value\""));
        assertTrue(output.contains("\"field2\":\"value2\""));
    }

    @Test
    void testBuildCommandWithQuotedFields() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "\"field\"", "30", "\"field2\"", "\"John\"");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Numeric value should not have quotes
        assertTrue(output.contains("\"field\":30"));
        assertFalse(output.contains("\"field\":\"30\""));
        // Quoted string value should have quotes
        assertTrue(output.contains("\"field2\":\"John\""));
    }

    @Test
    void testBuildCommandWithNumericValues() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "age", "25", "price", "19.99", "count", "100");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // All numeric values should not have quotes
        assertTrue(output.contains("\"age\":25"));
        assertFalse(output.contains("\"age\":\"25\""));
        assertTrue(output.contains("\"price\":19.99"));
        assertFalse(output.contains("\"price\":\"19.99\""));
        assertTrue(output.contains("\"count\":100"));
        assertFalse(output.contains("\"count\":\"100\""));
    }

    @Test
    void testBuildCommandWithMixedTypes() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "name", "John", "age", "30", "active", "true");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"John\""));
        assertTrue(output.contains("\"age\":30"));
        assertFalse(output.contains("\"age\":\"30\""));
        // "true" is not a number, so it should be a string
        assertTrue(output.contains("\"active\":\"true\""));
    }

    @Test
    void testBuildCommandWithQuotedStringValues() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "field", "\"John\"", "field2", "30");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        // Quoted string should remain as string
        assertTrue(output.contains("\"field\":\"John\""));
        // Unquoted number should be numeric
        assertTrue(output.contains("\"field2\":30"));
        assertFalse(output.contains("\"field2\":\"30\""));
    }

    @Test
    void testBuildCommandOddNumberOfArguments() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "field", "value", "field2");
        
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error"));
        assertTrue(errorOutput.contains("even number of arguments"));
    }

    @Test
    void testBuildCommandSinglePair() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "name", "Alice");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"Alice\""));
    }

    @Test
    void testBuildCommandEmptyFieldName() {
        ResultGeneratorCli cli = new ResultGeneratorCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("build", "\"\"", "value");
        
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error"));
        assertTrue(errorOutput.contains("Field name cannot be empty"));
    }
}

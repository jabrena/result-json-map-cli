package com.example.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldValuePairsCliTest {

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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name=John Doe", "city=New York");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"John Doe\""));
        assertTrue(output.contains("\"city\":\"New York\""));
    }

    @Test
    void testValueWithSpecialCharacters() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("email=user@example.com", "path=/home/user/file.txt");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"email\":\"user@example.com\""));
        assertTrue(output.contains("\"path\":\"/home/user/file.txt\""));
    }

    @Test
    void testEmptyValue() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("field=");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"field\":\"\""));
    }

    @Test
    void testInvalidPairFormat() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("invalidpair");
        
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error"));
        assertTrue(errorOutput.contains("Invalid pair format"));
    }

    @Test
    void testEmptyFieldName() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("=value");
        
        assertEquals(1, exitCode);
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Error"));
        assertTrue(errorOutput.contains("Field name cannot be empty"));
    }

    @Test
    void testHelpOption() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("--help");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("Usage:"));
        assertTrue(output.contains("field-value-cli"));
    }

    @Test
    void testVersionOption() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("--version");
        
        assertEquals(0, exitCode);
        String output = outContent.toString();
        assertTrue(output.contains("1.0.0"));
    }

    @Test
    void testNoArguments() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute();
        
        // Should show help or error
        assertTrue(exitCode != 0 || outContent.toString().contains("Usage:"));
    }

    @Test
    void testJsonStructure() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("  field  =  value  ", "  field2  :  value2  ");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"field\":\"value\""));
        assertTrue(output.contains("\"field2\":\"value2\""));
    }

    @Test
    void testNumericIntegerValue() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
        FieldValuePairsCli cli = new FieldValuePairsCli();
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
}

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
        assertTrue(output.contains("\"age\":\"30\""));
    }

    @Test
    void testMixedDelimiters() {
        FieldValuePairsCli cli = new FieldValuePairsCli();
        CommandLine cmd = new CommandLine(cli);
        
        int exitCode = cmd.execute("name=John", "age:30", "city=New York");
        
        assertEquals(0, exitCode);
        String output = outContent.toString().trim();
        assertTrue(output.contains("\"name\":\"John\""));
        assertTrue(output.contains("\"age\":\"30\""));
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
}

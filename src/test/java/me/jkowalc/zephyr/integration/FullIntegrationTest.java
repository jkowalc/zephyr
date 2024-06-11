package me.jkowalc.zephyr.integration;

import me.jkowalc.zephyr.MockTextPrinter;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.interpreter.Interpreter;
import me.jkowalc.zephyr.lexer.Lexer;
import me.jkowalc.zephyr.parser.CommentFilter;
import me.jkowalc.zephyr.parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.*;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullIntegrationTest {
    private String output;
    private void executeProgram(String input) throws IOException, ZephyrException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Parser parser = new Parser(new CommentFilter(lexer));
        Program program = parser.parseProgram();
        MockTextPrinter outputstream = new MockTextPrinter();
        Interpreter interpreter = new Interpreter(outputstream);
        program.accept(interpreter);
        output = outputstream.getText();
    }
    @Test
    public void testHelloWorld() throws ZephyrException, IOException {
        executeProgram("main() { print(\"Hello, World!\") }");
        assertEquals("Hello, World!", output);
    }
}

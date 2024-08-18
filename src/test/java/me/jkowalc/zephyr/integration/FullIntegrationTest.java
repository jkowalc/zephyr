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
    private int programExitCode;

    private void executeProgram(String input) throws IOException, ZephyrException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Parser parser = new Parser(new CommentFilter(lexer));
        Program program = parser.parseProgram();
        MockTextPrinter outputstream = new MockTextPrinter();
        Interpreter interpreter = new Interpreter(program, outputstream);
        programExitCode = interpreter.executeMain();
        output = outputstream.getText();
    }

    @Test
    public void testHelloWorld() throws ZephyrException, IOException {
        executeProgram("main() { printLn(\"Hello, World!\") }");
        assertEquals(0, programExitCode);
        assertEquals("Hello, World!\n", output);
    }

    @Test
    public void testScope() throws ZephyrException, IOException {
        executeProgram("""
                main() {
                    int a = 5;
                    {
                        int a = 2;
                        printLn(a); // 2
                    }
                    printLn(a); // 5
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("2\n5\n", output);
    }

    @Test
    public void testReference() throws ZephyrException, IOException {
        executeProgram("""
                func(int mref a) {
                    a = a + 5;
                }
                main() -> int {
                    int mut a = 5;
                    printLn(a); // 5
                    func(a); // cast to reference
                    printLn(a); // 10
                    return 0;
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("5\n10\n", output);

        executeProgram("""
                func(int mut a) {
                    a = a + 5;
                    printLn(a) // 10
                }
                main() {
                    int mut a = 5;
                    func(a);
                    printLn(a); // 5
                    a = a + 5;
                    printLn(a); // 10
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("10\n5\n10\n", output);
    }

    @Test
    public void testStructsAndUnions() throws ZephyrException, IOException {
        executeProgram("""
                struct Result {
                    result: int
                }
                struct Error {
                    errno: int
                }
                union Maybe { Result, Error }
                do_something() -> Maybe {
                    return {result: 1};
                }
                main() {
                    Maybe maybe = do_something();
                    match(maybe) {
                        case (Result res) {
                            printLn("Result: " + res.result);
                        }
                        case (Error err) {
                            printLn("Error: " + err.errno);
                        }
                    }
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("Result: 1\n", output);
    }

    @Test
    public void testWhile() throws ZephyrException, IOException {
        executeProgram("""
                main() {
                    int mut i = 0;
                    while(i < 10) {
                        printLn(i);
                        i = i + 1;
                    }
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n", output);
    }

    @Test
    public void testIf() throws ZephyrException, IOException {
        executeProgram("""
                main() {
                    int a = 5;
                    if (a > 10) {
                        printLn("Bigger than 10");
                    }
                    elif (a > 4) {
                        printLn("Bigger than 4");
                    }
                    else {
                        printLn("Some other number");
                    }
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("Bigger than 4\n", output);
    }

    @Test
    public void testRecursion() throws ZephyrException, IOException {
        executeProgram("""
                fib(int n) -> int {
                    if(n <= 1) {
                        return n;
                    }
                    else {
                        return fib(n-1) + fib(n-2);
                    }
                    return 0;
                }
                main() {
                    printLn(fib(10));
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("55\n", output);
    }

    @Test
    public void testFunctionCall() throws ZephyrException, IOException {
        executeProgram("""
                add(int a, int b) -> int {
                    return a + b;
                }
                main() {
                    int result = add(3, 4);
                    printLn(result);
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("7\n", output);
    }

    @Test
    public void testNestedFunctions() throws ZephyrException, IOException {
        executeProgram("""
                multiply(int a, int b) -> int {
                    return a * b;
                }
                addAndMultiply(int a, int b, int c) -> int {
                    return multiply(a + b, c);
                }
                main() {
                    int result = addAndMultiply(2, 3, 4);
                    printLn(result);
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("20\n", output);
    }

    @Test
    public void testStringConcatenation() throws ZephyrException, IOException {
        executeProgram("""
                main() {
                    string hello = "Hello, ";
                    string world = "World!";
                    printLn(hello + world);
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("Hello, World!\n", output);
    }

    @Test
    public void testBooleanLogic() throws ZephyrException, IOException {
        executeProgram("""
                main() {
                    bool a = true;
                    bool b = false;
                    if (a and !b) {
                        printLn("True");
                    } else {
                        printLn("False");
                    }
                    printLn(true or !true);
                    printLn(!true);
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("True\ntrue\nfalse\n", output);
    }

    @Test
    public void testObjectManipulation() throws ZephyrException, IOException {
        executeProgram("""
                struct GameState {
                    health: int,
                    finished: bool
                }
                finish(GameState mref state) {
                    state.finished = true;
                }
                main() {
                    GameState mut state = {health: 100, finished: false};
                    finish(state);
                    printLn(state.finished);
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("true\n", output);
    }

    @Test
    public void testExplicitTypeConversion() throws ZephyrException, IOException {
        executeProgram("""
                main() {
                    int a = 5;
                    float b = 3.14;
                    printLn(a + to_int(b));
                    printLn(to_float(a) + b);
                }
                """);
        assertEquals(0, programExitCode);
        assertEquals("8\n8.14\n", output);
    }
}

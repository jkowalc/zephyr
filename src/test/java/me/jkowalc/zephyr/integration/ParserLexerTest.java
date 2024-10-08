package me.jkowalc.zephyr.integration;

import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.AddExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.DotExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.LessEqualExpression;
import me.jkowalc.zephyr.domain.node.expression.binary.SubtractExpression;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import me.jkowalc.zephyr.lexer.Lexer;
import me.jkowalc.zephyr.parser.CommentFilter;
import me.jkowalc.zephyr.parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserLexerTest {
    private Parser parser;
    private void initTest(String input) throws IOException, LexicalException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        this.parser = new Parser(new CommentFilter(lexer));
    }
    @Test
    public void testComment() throws LexicalException, IOException, SyntaxException {
        initTest("main() {\n// comment\n print(\"hello print\") // comment 2 \n}");
        Program program = parser.parseProgram();
        Program expected = new Program(List.of(
                new FunctionDefinition("main", List.of(),
                        new StatementBlock(List.of(
                                new FunctionCall("print", List.of(new StringLiteral("hello print")))
                                )),
                        null
                )
        ), List.of());
        assertEquals(expected, program);
    }
    @Test
    public void testFibonacci() throws LexicalException, IOException, SyntaxException {
        String input = """
                fib(int n) -> int {
                    if(n <= 1) {
                        return n;
                    }
                    else {
                        return fib(n-1) + fib(n-2);
                    }
                }
                """;
        initTest(input);
        Program program = parser.parseProgram();
        Program expected = new Program(List.of(
                new FunctionDefinition("fib", List.of(
                        new VariableDefinition("n", "int", false, false, null)
                ), new StatementBlock(List.of(
                        new IfStatement(
                                new LessEqualExpression(
                                        new VariableReference("n"),
                                        new IntegerLiteral(1)
                                ),
                                new StatementBlock(List.of(
                                        new ReturnStatement(
                                                new VariableReference("n")
                                        )
                                )),
                                new StatementBlock(List.of(
                                        new ReturnStatement(
                                                new AddExpression(
                                                        new FunctionCall(
                                                                "fib",
                                                                List.of(
                                                                        new SubtractExpression(
                                                                                new VariableReference("n"),
                                                                                new IntegerLiteral(1)
                                                                        )
                                                                )
                                                        ),
                                                        new FunctionCall(
                                                                "fib",
                                                                List.of(
                                                                        new SubtractExpression(
                                                                                new VariableReference("n"),
                                                                                new IntegerLiteral(2)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                ))
                        )
                )), "int")
        ), List.of());
        assertEquals(expected, program);
    }
    @Test
    public void testStructsUnions() throws LexicalException, IOException, SyntaxException {
        String input = """
                struct SomeStruct {
                     a: int,
                     b: float
                 }
                 union SomeUnion { SomeStruct, int }
                 main() {
                     SomeStruct someStruct = {a: 1, b: 1.5};
                     someStruct.a = 1; // error, the variable someStruct is immutable
                     SomeUnion mut a = {a: 1, b: 1.5}; // ok
                     a = 1; // ok, change the actual type to int
                 }
               """;
        initTest(input);
        Program program = parser.parseProgram();
        Program expected = new Program(List.of(
                new FunctionDefinition("main", List.of(), new StatementBlock(List.of(
                        new VariableDefinition("someStruct", "SomeStruct", false, false, new StructLiteral(List.of(
                                new StructLiteralMember("a", new IntegerLiteral(1)),
                                new StructLiteralMember("b", new FloatLiteral(1.5f))
                        ))),
                        new AssignmentStatement(new DotExpression(new VariableReference("someStruct"), "a"), new IntegerLiteral(1)),
                        new VariableDefinition("a", "SomeUnion", true, false, new StructLiteral(List.of(
                                new StructLiteralMember("a", new IntegerLiteral(1)),
                                new StructLiteralMember("b", new FloatLiteral(1.5f))
                        ))),
                        new AssignmentStatement(new VariableReference("a"), new IntegerLiteral(1))
                )), null)
        ), List.of(
                new StructDefinition("SomeStruct", List.of(
                        new StructDefinitionMember("a", "int"),
                        new StructDefinitionMember("b", "float")
                )),
                new UnionDefinition("SomeUnion", List.of(
                        "SomeStruct", "int"
                ))
        ));
        assertEquals(expected, program);
    }
}

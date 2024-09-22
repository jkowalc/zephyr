package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.program.*;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.node.statement.VariableDefinition;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.MissingTokenException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseProgramTest {
    private Parser parser;
    private void initParser(List<Token> tokens) throws LexicalException, IOException {
        parser = new Parser(new MockLexer(tokens));
    }
    private void testFunctionDefinition(FunctionDefinition expected) throws LexicalException, SyntaxException, IOException {
        Program program = parser.parseProgram();
        assertEquals(1, program.getFunctions().size());
        assertEquals(0, program.getTypes().size());
        assertEquals(expected, program.getFunctions().toArray()[0]);
    }
    private void testTypeDefinition(TypeDefinition expected) throws LexicalException, SyntaxException, IOException {
        Program program = parser.parseProgram();
        assertEquals(0, program.getFunctions().size());
        assertEquals(1, program.getTypes().size());
        assertEquals(expected, program.getTypes().toArray()[0]);
    }
    @Test
    public void testFunctionDefinition() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("main"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testFunctionDefinition(new FunctionDefinition("main", List.of(), new StatementBlock(List.of()), null));

        initParser(List.of(
                new IdentifierToken("main"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IdentifierToken("int"),
                new IdentifierToken("a"),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testFunctionDefinition(new FunctionDefinition("main", List.of(
                new VariableDefinition("a", "int", false, false, null)
        ), new StatementBlock(List.of()), null));

        initParser(List.of(
                new IdentifierToken("main"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.ARROW),
                new IdentifierToken("int"),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testFunctionDefinition(new FunctionDefinition("main", List.of(), new StatementBlock(List.of()), "int"));
    }
    @Test
    public void testUnionDefinition() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.UNION),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new UnionDefinition("A", List.of()));

        initParser(List.of(
                new Token(TokenType.UNION),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("int"),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new UnionDefinition("A", List.of("int")));

        initParser(List.of(
                new Token(TokenType.UNION),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("int"),
                new Token(TokenType.COMMA),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new UnionDefinition("A", List.of("int")));

        initParser(List.of(
                new Token(TokenType.UNION),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("int"),
                new Token(TokenType.COMMA),
                new IdentifierToken("a"),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new UnionDefinition("A", List.of("int", "a")));
    }
    @Test
    public void testStructDefinition() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.STRUCT),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new StructDefinition("A", List.of()));

        initParser(List.of(
                new Token(TokenType.STRUCT),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("a"),
                new Token(TokenType.COLON),
                new IdentifierToken("int"),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new StructDefinition("A", List.of(
                new StructDefinitionMember("a", "int")
        )));

        initParser(List.of(
                new Token(TokenType.STRUCT),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("a"),
                new Token(TokenType.COLON),
                new IdentifierToken("int"),
                new Token(TokenType.COMMA),
                new IdentifierToken("b"),
                new Token(TokenType.COLON),
                new IdentifierToken("int"),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testTypeDefinition(new StructDefinition("A", List.of(
                new StructDefinitionMember("a", "int"),
                new StructDefinitionMember("b", "int")
        )));
    }
    @Test
    public void testStructMissingTokens() throws LexicalException, IOException {
        initParser(List.of(
                new Token(TokenType.STRUCT),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        assertThrows(MissingTokenException.class, () -> parser.parseProgram());

        initParser(List.of(
                new Token(TokenType.STRUCT),
                new IdentifierToken("A"),
                new Token(TokenType.CLOSE_BRACE)
        ));
        assertThrows(SyntaxException.class, () -> parser.parseProgram());
    }
    @Test
    public void testIncorrectStructMember() throws LexicalException, IOException {
        initParser(List.of(
                new Token(TokenType.STRUCT),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("int"),
                new Token(TokenType.CLOSE_BRACE)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseProgram()
        );

        initParser(List.of(
                new Token(TokenType.STRUCT),
                new IdentifierToken("A"),
                new Token(TokenType.OPEN_BRACE),
                new IntegerLiteralToken(1),
                new Token(TokenType.CLOSE_BRACE)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseProgram()
        );
    }
}

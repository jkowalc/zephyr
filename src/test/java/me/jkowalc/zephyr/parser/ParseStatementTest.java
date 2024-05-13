package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.statement.Statement;
import me.jkowalc.zephyr.domain.node.statement.StatementBlock;
import me.jkowalc.zephyr.domain.node.statement.VariableDefinition;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseStatementTest {
    private Parser parser;
    private void initParser(List<Token> tokens) throws LexicalException, IOException {
        // add braces to make it a block
        List<Token> tokensCopy = new ArrayList<>(tokens);
        tokensCopy.addFirst(new Token(TokenType.OPEN_BRACE));
        tokensCopy.addLast(new Token(TokenType.CLOSE_BRACE));
        this.parser = new Parser(new MockLexer(tokensCopy));
    }
    private void testStatement(Statement expected) throws LexicalException, IOException, SyntaxException {
        StatementBlock block = parser.parseStatementBlock();
        assertEquals(1, block.getStatements().size());
        assertEquals(expected, block.getStatements().getFirst());
    }
    @Test
    public void testVariableDefinition() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("int"),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        testStatement(new VariableDefinition("a", "int", false, false, new IntegerLiteral(1)));

        initParser(List.of(
                new IdentifierToken("int"),
                new Token(TokenType.MUT),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        testStatement(new VariableDefinition("a", "int", true, false, new IntegerLiteral(1)));
    }
    @Test
    public void testVariableDefinitionNoInitialization() throws LexicalException, IOException {
        initParser(List.of(
                new IdentifierToken("int"),
                new IdentifierToken("a")
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );
    }
    @Test
    public void testVariableDefinitionWrongModifier() throws LexicalException, IOException {
        initParser(List.of(
                new IdentifierToken("int"),
                new Token(TokenType.REF),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );

        initParser(List.of(
                new IdentifierToken("int"),
                new Token(TokenType.MREF),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );
    }
    @Test
    public void testFunctionCallStatement() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("foo"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.CLOSE_PARENTHESIS)
        ));
        testStatement(new FunctionCall("foo", List.of()));

        initParser(List.of(
                new IdentifierToken("foo"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IntegerLiteralToken(1),
                new Token(TokenType.CLOSE_PARENTHESIS)
        ));
        testStatement(new FunctionCall("foo", List.of(
                new IntegerLiteral(1)
        )));

        // Although not recommended, leaving a trailing comma is allowed
        initParser(List.of(
                new IdentifierToken("foo"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IntegerLiteralToken(1),
                new Token(TokenType.COMMA),
                new Token(TokenType.CLOSE_PARENTHESIS)
        ));
        testStatement(new FunctionCall("foo", List.of(
                new IntegerLiteral(1)
        )));
    }
}

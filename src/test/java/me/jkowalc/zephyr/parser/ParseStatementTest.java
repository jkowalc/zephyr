package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.DotExpression;
import me.jkowalc.zephyr.domain.node.expression.literal.BooleanLiteral;
import me.jkowalc.zephyr.domain.node.expression.literal.IntegerLiteral;
import me.jkowalc.zephyr.domain.node.statement.*;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.InvalidModifierException;
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
    private void testStatement(Statement expected) throws LexicalException, IOException, SyntaxException{
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
        assertThrows(InvalidModifierException.class, () ->
            parser.parseStatementBlock()
        );

        initParser(List.of(
                new IdentifierToken("int"),
                new Token(TokenType.MREF),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        assertThrows(InvalidModifierException.class, () ->
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

        initParser(List.of(
                new IdentifierToken("foo"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IntegerLiteralToken(1),
                new Token(TokenType.COMMA),
                new Token(TokenType.CLOSE_PARENTHESIS)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );
    }
    @Test
    public void testReturnStatement() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.RETURN)
        ));
        testStatement(new ReturnStatement(null));

        initParser(List.of(
                new Token(TokenType.RETURN),
                new IntegerLiteralToken(1)
        ));
        testStatement(new ReturnStatement(new IntegerLiteral(1)));

        initParser(List.of(
                new Token(TokenType.RETURN),
                new IntegerLiteralToken(1),
                new Token(TokenType.SEMICOLON)
        ));
        testStatement(new ReturnStatement(new IntegerLiteral(1)));
    }
    @Test
    public void testAssignmentStatement() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        testStatement(new AssignmentStatement(new VariableReference("a"), new IntegerLiteral(1)));
        initParser(List.of(
                new IdentifierToken("a"),
                new Token(TokenType.DOT),
                new IdentifierToken("b"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        testStatement(new AssignmentStatement(new DotExpression(new VariableReference("a"), "b"), new IntegerLiteral(1)));
    }
    @Test
    public void testAssignmentStatementException() throws LexicalException, IOException {
        initParser(List.of(
                new StringLiteralToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );

        initParser(List.of(
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );
    }
    @Test
    public void testIfStatement() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.IF),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.TRUE),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testStatement(new IfStatement(new BooleanLiteral(true), new StatementBlock(List.of(
                new AssignmentStatement(new VariableReference("a"), new IntegerLiteral(1))
        )), null));

        initParser(List.of(
                new Token(TokenType.IF),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.TRUE),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE),
                new Token(TokenType.ELSE),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testStatement(new IfStatement(new BooleanLiteral(true), new StatementBlock(List.of()), new StatementBlock(List.of())));

        initParser(List.of(
                new Token(TokenType.IF),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.TRUE),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE),
                new Token(TokenType.ELIF),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.FALSE),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));

        IfStatement ifStatement = new IfStatement(new BooleanLiteral(true), new StatementBlock(List.of()),
                new IfStatement(new BooleanLiteral(false), new StatementBlock(List.of()), null));
        testStatement(ifStatement);
    }
    @Test
    public void testWhileStatement() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.WHILE),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.TRUE),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testStatement(new WhileStatement(new BooleanLiteral(true), new StatementBlock(List.of(
                new AssignmentStatement(new VariableReference("a"), new IntegerLiteral(1))
        ))));
    }
    @Test
    public void testMatchStatement() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.MATCH),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IdentifierToken("a"),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CASE),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IdentifierToken("int"),
                new IdentifierToken("a"),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("b"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testStatement(new MatchStatement(new VariableReference("a"), List.of(
                new MatchCase("int", "a", new StatementBlock(List.of(
                        new AssignmentStatement(new VariableReference("b"), new IntegerLiteral(1)
                ))
        )))));
    }
    @Test
    public void testEmbeddedStatementBlock() throws LexicalException, IOException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testStatement(new StatementBlock(List.of()));

        initParser(List.of(
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1),
                new Token(TokenType.CLOSE_BRACE)
        ));
        testStatement(new StatementBlock(List.of(
                new AssignmentStatement(new VariableReference("a"), new IntegerLiteral(1))
        )));
    }
    @Test
    public void testDoubleSemicolon() throws LexicalException, IOException {
        initParser(List.of(
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1),
                new Token(TokenType.SEMICOLON),
                new Token(TokenType.SEMICOLON)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );
        initParser(List.of(
                new IdentifierToken("a"),
                new Token(TokenType.ASSIGNMENT),
                new IntegerLiteralToken(1),
                new Token(TokenType.SEMICOLON),
                new Token(TokenType.SEMICOLON),
                new Token(TokenType.SEMICOLON)
        ));
        assertThrows(SyntaxException.class, () ->
            parser.parseStatementBlock()
        );
    }
}

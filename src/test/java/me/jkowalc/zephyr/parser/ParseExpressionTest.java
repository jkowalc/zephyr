package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.domain.node.expression.FunctionCall;
import me.jkowalc.zephyr.domain.node.expression.VariableReference;
import me.jkowalc.zephyr.domain.node.expression.binary.*;
import me.jkowalc.zephyr.domain.node.expression.literal.*;
import me.jkowalc.zephyr.domain.node.expression.unary.NegationExpression;
import me.jkowalc.zephyr.domain.node.expression.unary.NotExpression;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.syntax.MultipleComparisonException;
import me.jkowalc.zephyr.exception.syntax.SyntaxException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseExpressionTest {
    private Parser parser;

    private void initParser(List<Token> tokens) throws LexicalException, IOException {
        this.parser = new Parser(new MockLexer(tokens));
    }

    @Test
    public void testLiteral() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IntegerLiteralToken(123)
        ));
        Expression expression = parser.parseExpression();
        assertEquals(new IntegerLiteral(123), expression);

        initParser(List.of(new FloatLiteralToken(123.456f)));
        assertEquals(new FloatLiteral(123.456f), parser.parseExpression());

        initParser(List.of(new StringLiteralToken("abc")));
        assertEquals(new StringLiteral("abc"), parser.parseExpression());

        initParser(List.of(new Token(TokenType.TRUE)));
        assertEquals(new BooleanLiteral(true), parser.parseExpression());
        initParser(List.of(new Token(TokenType.FALSE)));
        assertEquals(new BooleanLiteral(false), parser.parseExpression());
    }

    @Test
    public void testStandardOperators() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.OR),
                new IntegerLiteralToken(2)
        ));
        Expression expected = new OrExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.AND),
                new IntegerLiteralToken(2)
        ));
        expected = new AndExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.LESS),
                new IntegerLiteralToken(2)
        ));
        expected = new LessExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.LESS_EQUALS),
                new IntegerLiteralToken(2)
        ));
        expected = new LessEqualExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.GREATER),
                new IntegerLiteralToken(2)
        ));
        expected = new GreaterExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.GREATER_EQUALS),
                new IntegerLiteralToken(2)
        ));
        expected = new GreaterEqualExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.EQUALS),
                new IntegerLiteralToken(2)
        ));
        expected = new EqualExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.NOT_EQUALS),
                new IntegerLiteralToken(2)
        ));
        expected = new NotEqualExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.PLUS),
                new IntegerLiteralToken(2)
        ));
        expected = new AndExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.DIVIDE),
                new IntegerLiteralToken(2)
        ));
        expected = new DivideExpression(new IntegerLiteral(1), new IntegerLiteral(2));
        assertEquals(expected, parser.parseExpression());
    }

    @Test
    public void testMultipleComparisons() throws LexicalException, IOException {
        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.LESS),
                new IntegerLiteralToken(2),
                new Token(TokenType.LESS),
                new IntegerLiteralToken(3)
        ));
        assertThrows(MultipleComparisonException.class, () -> parser.parseExpression());
    }

    @Test
    public void testPrecedence() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.PLUS),
                new IntegerLiteralToken(2),
                new Token(TokenType.MULTIPLY),
                new IntegerLiteralToken(3)
        ));
        Expression expected = new AddExpression(
                new IntegerLiteral(1),
                new MultiplyExpression(new IntegerLiteral(2), new IntegerLiteral(3))
        );
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.MULTIPLY),
                new IntegerLiteralToken(2),
                new Token(TokenType.PLUS),
                new IntegerLiteralToken(3)
        ));
        expected = new AddExpression(
                new MultiplyExpression(new IntegerLiteral(1), new IntegerLiteral(2)),
                new IntegerLiteral(3)
        );
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new Token(TokenType.TRUE),
                new Token(TokenType.AND),
                new Token(TokenType.FALSE),
                new Token(TokenType.OR),
                new Token(TokenType.TRUE)
        ));
        expected = new OrExpression(
                new AndExpression(new BooleanLiteral(true), new BooleanLiteral(false)),
                new BooleanLiteral(true)
        );
        assertEquals(expected, parser.parseExpression());
    }

    @Test
    public void testLinking() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.PLUS),
                new IntegerLiteralToken(2),
                new Token(TokenType.MINUS),
                new IntegerLiteralToken(3)
        ));
        Expression expected = new SubtractExpression(
                new AddExpression(new IntegerLiteral(1), new IntegerLiteral(2)),
                new IntegerLiteral(3)
        );
        assertEquals(expected, parser.parseExpression());

        initParser(List.of(
                new Token(TokenType.TRUE),
                new Token(TokenType.AND),
                new Token(TokenType.TRUE),
                new Token(TokenType.AND),
                new Token(TokenType.FALSE)
        ));
        expected = new AndExpression(
                new AndExpression(new BooleanLiteral(true), new BooleanLiteral(true)),
                new BooleanLiteral(false)
        );
        assertEquals(expected, parser.parseExpression());
    }

    @Test
    public void testParentheses() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IntegerLiteralToken(2),
                new Token(TokenType.MULTIPLY),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IntegerLiteralToken(1),
                new Token(TokenType.PLUS),
                new IntegerLiteralToken(2),
                new Token(TokenType.CLOSE_PARENTHESIS)
        ));
        Expression expected = new MultiplyExpression(
                new IntegerLiteral(2),
                new AddExpression(new IntegerLiteral(1), new IntegerLiteral(2))
        );
        assertEquals(expected, parser.parseExpression());
    }
    @Test
    public void testFunctionCall() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("hello"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IntegerLiteralToken(1),
                new Token(TokenType.COMMA),
                new IntegerLiteralToken(2),
                new Token(TokenType.CLOSE_PARENTHESIS)
        ));
        Expression expected = new FunctionCall(
                "hello",
                List.of(new IntegerLiteral(1), new IntegerLiteral(2))
        );
        Expression actual = parser.parseExpression();
        assertEquals(expected, actual);
    }
    @Test
    public void testVariableReference() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("hello")
        ));
        Expression expected = new VariableReference("hello");
        Expression actual = parser.parseExpression();
        assertEquals(expected, actual);
    }
    @Test
    public void testDotExpression() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new IdentifierToken("hello"),
                new Token(TokenType.DOT),
                new IdentifierToken("world")
        ));
        Expression expected = new DotExpression(
                new VariableReference("hello"),
                "world"
        );
        Expression actual = parser.parseExpression();
        assertEquals(expected, actual);

        initParser(List.of(
                new IdentifierToken("hello"),
                new Token(TokenType.DOT),
                new IdentifierToken("world"),
                new Token(TokenType.DOT),
                new IdentifierToken("foo")
        ));
        expected = new DotExpression(
                new DotExpression(
                        new VariableReference("hello"),
                        "world"
                ),
                "foo"
        );
        actual = parser.parseExpression();
        assertEquals(expected, actual);
    }
    @Test
    public void testDotExpressionError() throws IOException, LexicalException {
        initParser(List.of(
                new IdentifierToken("hello"),
                new Token(TokenType.DOT)
        ));
        assertThrows(SyntaxException.class, () -> parser.parseExpression());
    }
    @Test
    public void testStructLiteral() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("hello"),
                new Token(TokenType.COLON),
                new IntegerLiteralToken(1),
                new Token(TokenType.COMMA),
                new IdentifierToken("world"),
                new Token(TokenType.COLON),
                new IntegerLiteralToken(2),
                new Token(TokenType.CLOSE_BRACE)
        ));
        Expression expected = new StructLiteral(
                Map.of(
                        "hello", new IntegerLiteral(1),
                        "world", new IntegerLiteral(2)
                )
        );
        Expression actual = parser.parseExpression();
        assertEquals(expected, actual);
    }
    @Test
    public void testEmbeddedStruct() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("hello"),
                new Token(TokenType.COLON),
                new IntegerLiteralToken(1),
                new Token(TokenType.COMMA),
                new IdentifierToken("world"),
                new Token(TokenType.COLON),
                new Token(TokenType.OPEN_BRACE),
                new IdentifierToken("foo"),
                new Token(TokenType.COLON),
                new IntegerLiteralToken(2),
                new Token(TokenType.CLOSE_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        ));
        Expression expected = new StructLiteral(
                Map.of(
                        "hello", new IntegerLiteral(1),
                        "world", new StructLiteral(
                                Map.of("foo", new IntegerLiteral(2))
                        )
                )
        );
        Expression actual = parser.parseExpression();
        assertEquals(expected, actual);
    }
    @Test
    public void testUnterminatedEmbedded() throws LexicalException, IOException {
        initParser(List.of(
                new IntegerLiteralToken(1),
                new Token(TokenType.PLUS),
                new Token(TokenType.OPEN_PARENTHESIS),
                new IntegerLiteralToken(2)
        ));
        assertThrows(SyntaxException.class, () -> parser.parseExpression());
    }
    @Test
    public void testUnary() throws IOException, LexicalException, SyntaxException {
        initParser(List.of(
                new Token(TokenType.MINUS),
                new IntegerLiteralToken(1)
        ));
        Expression expected = new NegationExpression(new IntegerLiteral(1));
        Expression actual = parser.parseExpression();
        assertEquals(expected, actual);

        initParser(List.of(
                new Token(TokenType.NOT),
                new Token(TokenType.TRUE)
        ));
        expected = new NotExpression(new BooleanLiteral(true));
        assertEquals(expected, parser.parseExpression());
    }
    @Test
    public void testUnterminatedUnary() throws IOException, LexicalException {
        initParser(List.of(
                new Token(TokenType.MINUS)
        ));
        assertThrows(SyntaxException.class, () -> parser.parseExpression());

        initParser(List.of(
                new Token(TokenType.NOT)
        ));
        assertThrows(SyntaxException.class, () -> parser.parseExpression());
    }
}

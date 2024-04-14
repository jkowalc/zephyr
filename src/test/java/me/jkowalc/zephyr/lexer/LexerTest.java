package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.CommentToken;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.InvalidEscapeSequenceException;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    private void testSingleToken(String input, Token expected) throws IOException, LexicalException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Token token = lexer.nextToken();
        assertEquals(expected, token);
        token = lexer.nextToken();
        assertEquals(new Token(TokenType.EOF), token);
    }
    private void testSingleTokenFloat(String input, double expected) throws IOException, LexicalException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Token token = lexer.nextToken();
        assertInstanceOf(FloatLiteralToken.class, token);
        assertEquals(expected, ((FloatLiteralToken) token).getValue(), 0.0001);
        token = lexer.nextToken();
        assertEquals(new Token(TokenType.EOF), token);
    }

    @Test
    public void testIdentifier() throws IOException, LexicalException {
        testSingleToken("abc", new IdentifierToken("abc"));
    }
    @Test
    public void testNumberNormal() throws IOException, LexicalException {
        testSingleToken("0", new IntegerLiteralToken(0));
        testSingleToken("123", new IntegerLiteralToken(123));
        testSingleTokenFloat("123.456", 123.456);
    }
    @Test
    public void testNumberLeadingZero() {
        assertThrows(LexicalException.class, () -> testSingleToken("01", new IntegerLiteralToken(1)));
    }
    @Test
    public void testString() throws LexicalException, IOException {
        testSingleToken("\"abs\"", new StringLiteralToken("abs"));
    }
    @Test
    public void testStringEscaping() throws LexicalException, IOException {
        testSingleToken("\"\\\"b\"", new StringLiteralToken("\"b"));
    }
    @Test
    public void testInvalidEscaping() {
        assertThrows(InvalidEscapeSequenceException.class, () -> testSingleToken("\"\\a\"", new StringLiteralToken("a")));
    }
    @Test
    public void testComment() throws IOException, LexicalException {
        testSingleToken("//abs", new CommentToken("abs"));
    }
    @Test
    public void testKeyword() throws LexicalException, IOException {
        testSingleToken("if", new Token(TokenType.IF));
        testSingleToken("else", new Token(TokenType.ELSE));
        testSingleToken("while", new Token(TokenType.WHILE));
        testSingleToken("return", new Token(TokenType.RETURN));
    }
    @Test
    public void testOperator() throws LexicalException, IOException {
        testSingleToken("+", new Token(TokenType.PLUS));
        testSingleToken("-", new Token(TokenType.MINUS));
        testSingleToken("->", new Token(TokenType.ARROW));
        testSingleToken("==", new Token(TokenType.EQUALS));
    }
}

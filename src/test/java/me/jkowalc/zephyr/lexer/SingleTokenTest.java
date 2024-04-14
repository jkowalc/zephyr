package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.CommentToken;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.LexicalException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.*;

public class SingleTokenTest {
    private void testToken(String input, Token expected) throws IOException, LexicalException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Token token = lexer.nextToken();
        assertEquals(expected, token);
        token = lexer.nextToken();
        assertEquals(new Token(TokenType.EOF), token);
    }
    private void testTokenFloat(String input, double expected) throws IOException, LexicalException {
        Lexer lexer = new Lexer(getStringAsInputStreamReader(input));
        Token token = lexer.nextToken();
        assertInstanceOf(FloatLiteralToken.class, token);
        assertEquals(expected, ((FloatLiteralToken) token).getValue(), 0.0001);
        token = lexer.nextToken();
        assertEquals(new Token(TokenType.EOF), token);
    }

    @Test
    public void testIdentifier() throws IOException, LexicalException {
        testToken("abc", new IdentifierToken("abc"));
    }
    @Test
    public void testNumberNormal() throws IOException, LexicalException {
        testToken("0", new IntegerLiteralToken(0));
        testToken("123", new IntegerLiteralToken(123));
        testTokenFloat("123.456", 123.456);
    }
    @Test
    public void testNumberLeadingZero() {
        assertThrows(LexicalException.class, () -> testToken("01", new IntegerLiteralToken(1)));
    }
    @Test
    public void testStringNormal() throws LexicalException, IOException {
        testToken("\"abs\"", new StringLiteralToken("abs"));
    }
    @Test
    public void testStringEscaping() throws LexicalException, IOException {
        testToken("\"\\\"b\"", new StringLiteralToken("\"b"));
    }
    @Test
    public void testInvalidEscaping() {
        assertThrows(LexicalException.class, () -> testToken("\"\\a\"", new StringLiteralToken("a")));
    }
    @Test
    public void testComment() throws IOException, LexicalException {
        testToken("//abs", new CommentToken("abs"));
    }
    @Test
    public void testKeyword() throws LexicalException, IOException {
        testToken("if", new Token(TokenType.IF));
        testToken("else", new Token(TokenType.ELSE));
        testToken("while", new Token(TokenType.WHILE));
        testToken("return", new Token(TokenType.RETURN));
    }
    @Test
    public void testOperator() throws LexicalException, IOException {
        testToken("+", new Token(TokenType.PLUS));
        testToken("-", new Token(TokenType.MINUS));
        testToken("->", new Token(TokenType.ARROW));
        testToken("==", new Token(TokenType.EQUALS));
    }
}

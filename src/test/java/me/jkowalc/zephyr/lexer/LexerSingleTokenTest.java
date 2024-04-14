package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.CommentToken;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.FloatLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.domain.token.literal.StringLiteralToken;
import me.jkowalc.zephyr.exception.lexical.InvalidEscapeSequenceException;
import me.jkowalc.zephyr.exception.lexical.InvalidNumberException;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.exception.lexical.TokenTooLongException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static me.jkowalc.zephyr.lexer.Lexer.*;
import static org.junit.jupiter.api.Assertions.*;

public class LexerSingleTokenTest {
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
    public void testIdentifierTooLong() throws LexicalException, IOException {
        testSingleToken("a".repeat(MAX_IDENTIFIER_LENGTH), new IdentifierToken("a".repeat(MAX_IDENTIFIER_LENGTH)));
        assertThrows(LexicalException.class, () -> testSingleToken("a".repeat(MAX_IDENTIFIER_LENGTH + 1), new IdentifierToken("a".repeat(MAX_IDENTIFIER_LENGTH))));
    }
    @Test
    public void testNumberNormal() throws IOException, LexicalException {
        testSingleToken("0", new IntegerLiteralToken(0));
        testSingleToken("123", new IntegerLiteralToken(123));
        testSingleTokenFloat("123.456", 123.456);
    }
    @Test
    public void testNumberTooLong() throws LexicalException, IOException {
        testSingleTokenFloat("1" + "." + "0".repeat(MAX_NUMBER_LENGTH - 2), 1.0);
        assertThrows(TokenTooLongException.class, () -> testSingleTokenFloat("1" + "." + "0".repeat(MAX_NUMBER_LENGTH - 1), 1.0));
    }
    @Test
    public void testInvalidNumber(){
        assertThrows(InvalidNumberException.class, () -> testSingleToken("1.2.3", new IntegerLiteralToken(1)));
    }
    @Test
    public void testInvalidNumberTooLong() {
        // the number cannot be parsed as integer
        assertThrows(InvalidNumberException.class, () -> testSingleToken("1" + "0".repeat(MAX_NUMBER_LENGTH-1), new IntegerLiteralToken(1)));
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
    public void testStringTooLong() throws IOException, LexicalException {
        testSingleToken("\"" + "a".repeat(MAX_STRING_LENGTH) + "\"", new StringLiteralToken("a".repeat(MAX_STRING_LENGTH)));
        assertThrows(LexicalException.class, () -> testSingleToken("\"" + "a".repeat(MAX_STRING_LENGTH + 1) + "\"", new StringLiteralToken("a".repeat(MAX_STRING_LENGTH))));
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
    public void testCommentTooLong() throws LexicalException, IOException {
        testSingleToken("//" + "a".repeat(400), new CommentToken("a".repeat(MAX_COMMENT_LENGTH)));
        assertThrows(LexicalException.class, () -> testSingleToken("//" + "a".repeat(MAX_COMMENT_LENGTH + 1), new CommentToken("a".repeat(400))));
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

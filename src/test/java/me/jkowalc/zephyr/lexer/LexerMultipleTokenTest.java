package me.jkowalc.zephyr.lexer;

import me.jkowalc.zephyr.domain.token.CommentToken;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.domain.token.literal.IntegerLiteralToken;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.jkowalc.zephyr.Utils.getStringAsInputStreamReader;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerMultipleTokenTest {
    private Lexer lexer;
    private void initLexer(String input) throws IOException {
        lexer = new Lexer(getStringAsInputStreamReader(input));
    }
    private void testMultipleTokens(String input, Token... expected) throws IOException, LexicalException {
        initLexer(input);
        for (Token token : expected) {
            assertEquals(token, lexer.nextToken());
        }
        assertEquals(new Token(TokenType.EOF), lexer.nextToken());
    }
    @Test
    public void testMultipleTokensBasic() throws IOException, LexicalException {
        testMultipleTokens("abc 123",
                new IdentifierToken("abc"),
                new IntegerLiteralToken(123));
        testMultipleTokens("123abc",
                new IntegerLiteralToken(123),
                new IdentifierToken("abc"));
    }
    @Test
    public void testCommentNewline() throws IOException, LexicalException {
        testMultipleTokens("abc\n//comment\n123",
                new IdentifierToken("abc"),
                new CommentToken("comment"),
                new IntegerLiteralToken(123));
    }

    @Test
    public void testExpressions() throws IOException, LexicalException {
        testMultipleTokens("1+2",
                new IntegerLiteralToken(1),
                new Token(TokenType.PLUS),
                new IntegerLiteralToken(2));
        testMultipleTokens("1/2",
                new IntegerLiteralToken(1),
                new Token(TokenType.DIVIDE),
                new IntegerLiteralToken(2));
    }
}

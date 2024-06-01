package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.token.CommentToken;
import me.jkowalc.zephyr.domain.token.IdentifierToken;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.lexer.LexerInterface;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentFilterTest {
    @Test
    public void testCommentFilter() throws LexicalException, IOException {
        LexerInterface lexer = new CommentFilter(new MockLexer(List.of(
                new IdentifierToken("main"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new CommentToken("comment"),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new CommentToken("comment 2"),
                new Token(TokenType.CLOSE_BRACE)
        )));
        List<Token> expected = List.of(
                new IdentifierToken("main"),
                new Token(TokenType.OPEN_PARENTHESIS),
                new Token(TokenType.CLOSE_PARENTHESIS),
                new Token(TokenType.OPEN_BRACE),
                new Token(TokenType.CLOSE_BRACE)
        );
        for (Token token : expected) {
            assertEquals(token, lexer.nextToken());
        }
    }
}

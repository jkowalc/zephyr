package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.lexer.LexerInterface;

import java.io.IOException;

public class CommentFilter implements LexerInterface {
    private final LexerInterface lexer;
    public CommentFilter(LexerInterface lexer) {
        this.lexer = lexer;
    }

    @Override
    public Token nextToken() throws IOException, LexicalException {
        Token token = lexer.nextToken();
        while (token.getType() == TokenType.COMMENT) {
            token = lexer.nextToken();
        }
        return token;
    }
}

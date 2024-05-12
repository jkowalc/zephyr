package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.exception.lexical.LexicalException;
import me.jkowalc.zephyr.lexer.LexerInterface;

import java.io.IOException;

public class TokenReader {
    private final LexerInterface lexer;
    private Token currentToken;
    public TokenReader(LexerInterface lexer) throws LexicalException, IOException {
        this.lexer = lexer;
        currentToken = lexer.nextToken();
    }
    public Token getToken() {
        return currentToken;
    }
    public TokenType getType() {
        return currentToken.getType();
    }
    public void next() throws LexicalException, IOException {
        currentToken = lexer.nextToken();
    }
}

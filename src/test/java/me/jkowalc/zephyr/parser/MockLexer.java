package me.jkowalc.zephyr.parser;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.lexer.LexerInterface;

import java.util.List;

public class MockLexer implements LexerInterface {
    private final List<Token> tokens;
    private int index = 0;
    public MockLexer(List<Token> tokens) {
        this.tokens = tokens;
    }
    @Override
    public Token nextToken() {
        if (index >= tokens.size()) {
            return new Token(TokenType.EOF);
        }
        return tokens.get(index++);
    }
}

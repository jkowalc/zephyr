package me.jkowalc.zephyr.domain.token;

import me.jkowalc.zephyr.util.TextPosition;

public class Token {
    private final TextPosition startPosition;
    private final TextPosition endPosition;
    private final TokenType type;

    public Token(TextPosition startPosition, TextPosition endPosition, TokenType type) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", type=" + type +
                '}';
    }
    public TextPosition getStartPosition() {
        return startPosition;
    }

    public TextPosition getEndPosition() {
        return endPosition;
    }

    public TokenType getType() {
        return type;
    }
}

package me.jkowalc.zephyr.domain.token;

import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;

public class Token {
    private final TextPosition startPosition;
    private final TextPosition endPosition;
    private final TokenType type;

    public Token(TextPosition startPosition, TextPosition endPosition, TokenType type) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.type = type;
    }

    public Token(TokenType type) {
        this.startPosition = null;
        this.endPosition = null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}

package me.jkowalc.zephyr.domain.token;

import lombok.Getter;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;

@Getter
public class Token {
    private final TextPosition startPosition;
    private final TokenType type;

    public Token(TextPosition startPosition, TokenType type) {
        this.startPosition = startPosition;
        this.type = type;
    }

    public Token(TokenType type) {
        this.startPosition = null;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "startPosition=" + startPosition +
                ", type=" + type +
                '}';
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

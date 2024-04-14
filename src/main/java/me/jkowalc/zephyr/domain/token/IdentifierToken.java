package me.jkowalc.zephyr.domain.token;

import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;

public class IdentifierToken extends Token {
    private final String value;
    public IdentifierToken(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition, TokenType.IDENTIFIER);
        this.value = value;
    }

    public IdentifierToken(String value) {
        super(null, null, TokenType.IDENTIFIER);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IdentifierToken{" +
                "startPosition=" + getStartPosition() +
                ", endPosition=" + getEndPosition() +
                ", value=\"" + value + '\"' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IdentifierToken that = (IdentifierToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}

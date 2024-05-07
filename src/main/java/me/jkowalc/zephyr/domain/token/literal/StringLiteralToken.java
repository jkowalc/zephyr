package me.jkowalc.zephyr.domain.token.literal;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

public class StringLiteralToken extends Token {
    private final String value;
    public StringLiteralToken(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition, TokenType.STRING_LITERAL);
        this.value = value;
    }

    public StringLiteralToken(String value) {
        super(null, null, TokenType.STRING_LITERAL);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StringLiteralToken{" +
                "startPosition=" + getStartPosition() +
                ", endPosition=" + getEndPosition() +
                ", value=\"" + getRepresentation(value) + '\"' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StringLiteralToken that = (StringLiteralToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}

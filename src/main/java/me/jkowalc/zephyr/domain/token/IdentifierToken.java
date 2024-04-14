package me.jkowalc.zephyr.domain.token;

import me.jkowalc.zephyr.util.TextPosition;

public class IdentifierToken extends Token {
    private final String value;
    public IdentifierToken(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition, TokenType.IDENTIFIER);
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
}

package me.jkowalc.zephyr.domain.token.literal;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

public class StringLiteralToken extends Token {
    private final String value;
    public StringLiteralToken(TextPosition startPosition, TextPosition endPosition, String value) {
        super(startPosition, endPosition, TokenType.STRING_LITERAL);
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
}

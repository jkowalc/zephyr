package me.jkowalc.zephyr.domain.token.literal;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;


public class FloatLiteralToken extends Token {
    private final float value;
    public FloatLiteralToken(TextPosition startPosition, TextPosition endPosition, String valueStr) {
        super(startPosition, endPosition, TokenType.FLOAT_LITERAL);
        this.value = Float.parseFloat(valueStr);
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FloatLiteralToken{" +
                "startPosition=" + getStartPosition() +
                ", endPosition=" + getEndPosition() +
                ", value=" + value +
                '}';
    }
}

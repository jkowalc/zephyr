package me.jkowalc.zephyr.domain.token.literal;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;


public class IntegerLiteralToken extends Token {
    private final int value;
    public IntegerLiteralToken(TextPosition startPosition, TextPosition endPosition, String valueStr) {
        super(startPosition, endPosition, TokenType.INTEGER_LITERAL);
        this.value = Integer.parseInt(valueStr);
    }
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IntegerLiteralToken{" +
                "value=" + value +
                '}';
    }
}

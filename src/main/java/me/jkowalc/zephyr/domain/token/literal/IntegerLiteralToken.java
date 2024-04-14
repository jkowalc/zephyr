package me.jkowalc.zephyr.domain.token.literal;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;


public class IntegerLiteralToken extends Token {
    private final int value;
    public IntegerLiteralToken(TextPosition startPosition, TextPosition endPosition, String valueStr) {
        super(startPosition, endPosition, TokenType.INTEGER_LITERAL);
        this.value = Integer.parseInt(valueStr);
    }
    public IntegerLiteralToken(String valueStr) {
        super(null, null, TokenType.INTEGER_LITERAL);
        this.value = Integer.parseInt(valueStr);
    }
    public IntegerLiteralToken(int value) {
        super(null, null, TokenType.INTEGER_LITERAL);
        this.value = value;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        IntegerLiteralToken that = (IntegerLiteralToken) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}

package me.jkowalc.zephyr.domain.token.literal;

import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.Objects;


public class FloatLiteralToken extends Token {
    private final double value;
    public FloatLiteralToken(TextPosition startPosition, TextPosition endPosition, String valueStr) {
        super(startPosition, endPosition, TokenType.FLOAT_LITERAL);
        this.value = Float.parseFloat(valueStr);
    }

    public FloatLiteralToken(String valueStr) {
        super(null, null, TokenType.FLOAT_LITERAL);
        this.value = Float.parseFloat(valueStr);
    }

    public FloatLiteralToken(double value) {
        super(null, null, TokenType.FLOAT_LITERAL);
        this.value = value;
    }

    public double getValue() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FloatLiteralToken that = (FloatLiteralToken) o;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}

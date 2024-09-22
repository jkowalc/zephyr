package me.jkowalc.zephyr.domain.token.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;


@Getter
@EqualsAndHashCode(callSuper = false)
public class FloatLiteralToken extends Token {
    private final float value;
    public FloatLiteralToken(TextPosition startPosition, float value) {
        super(startPosition, TokenType.FLOAT_LITERAL);
        this.value = value;
    }

    public FloatLiteralToken(float value) {
        super(null, TokenType.FLOAT_LITERAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return "FloatLiteralToken{" +
                "startPosition=" + getStartPosition() +
                ", value=" + value +
                '}';
    }
}

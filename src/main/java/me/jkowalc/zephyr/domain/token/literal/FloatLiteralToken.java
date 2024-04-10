package me.jkowalc.zephyr.domain.token.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;


@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class FloatLiteralToken extends Token {
    private final float value;
    public FloatLiteralToken(TextPosition position, float value) {
        super(position, TokenType.FLOAT_LITERAL);
        this.value = value;
    }
}

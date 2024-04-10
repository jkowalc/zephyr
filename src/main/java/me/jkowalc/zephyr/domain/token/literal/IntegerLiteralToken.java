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
public class IntegerLiteralToken extends Token {
    private final int value;
    public IntegerLiteralToken(TextPosition position, int value) {
        super(position, TokenType.INTEGER_LITERAL);
        this.value = value;
    }
}

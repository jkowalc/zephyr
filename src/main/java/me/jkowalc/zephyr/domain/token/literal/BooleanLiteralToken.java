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
public class BooleanLiteralToken extends Token {
    private final boolean value;
    public BooleanLiteralToken(TextPosition position, boolean value) {
        super(position, TokenType.BOOLEAN_LITERAL);
        this.value = value;
    }
}

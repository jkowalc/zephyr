package me.jkowalc.zephyr.domain.token.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;


@Getter
@EqualsAndHashCode(callSuper = false)
public class IntegerLiteralToken extends Token {
    private final int value;
    public IntegerLiteralToken(TextPosition startPosition, int value) {
        super(startPosition, TokenType.INTEGER_LITERAL);
        this.value = value;
    }
    public IntegerLiteralToken(int value) {
        super(null, TokenType.INTEGER_LITERAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return "IntegerLiteralToken{" +
                "value=" + value +
                '}';
    }
}

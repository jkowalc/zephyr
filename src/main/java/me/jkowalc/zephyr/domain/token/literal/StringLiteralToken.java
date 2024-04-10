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
public class StringLiteralToken extends Token {
    private final String value;
    public StringLiteralToken(TextPosition position, String value) {
        super(position, TokenType.STRING_LITERAL);
        this.value = value;
    }
}

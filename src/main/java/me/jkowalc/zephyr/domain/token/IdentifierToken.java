package me.jkowalc.zephyr.domain.token;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class IdentifierToken extends Token {
    private final String value;
    public IdentifierToken(TextPosition position, String value) {
        super(position, TokenType.IDENTIFIER);
        this.value = value;
    }
}

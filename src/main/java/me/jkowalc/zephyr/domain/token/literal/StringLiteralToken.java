package me.jkowalc.zephyr.domain.token.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.util.TextPosition;

import static me.jkowalc.zephyr.util.CharacterUtil.getRepresentation;

@Getter
@EqualsAndHashCode(callSuper = false)
public class StringLiteralToken extends Token {
    private final String value;
    public StringLiteralToken(TextPosition startPosition, String value) {
        super(startPosition, TokenType.STRING_LITERAL);
        this.value = value;
    }

    public StringLiteralToken(String value) {
        super(null, TokenType.STRING_LITERAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return "StringLiteralToken{" +
                "startPosition=" + getStartPosition() +
                ", value=\"" + getRepresentation(value) + '\"' +
                '}';
    }
}

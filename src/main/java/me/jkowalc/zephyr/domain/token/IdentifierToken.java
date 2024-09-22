package me.jkowalc.zephyr.domain.token;

import lombok.Getter;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public class IdentifierToken extends Token {
    private final String value;
    public IdentifierToken(TextPosition startPosition, String value) {
        super(startPosition, TokenType.IDENTIFIER);
        this.value = value;
    }

    public IdentifierToken(String value) {
        super(null, TokenType.IDENTIFIER);
        this.value = value;
    }

    @Override
    public String toString() {
        return "IdentifierToken{" +
                "startPosition=" + getStartPosition() +
                ", value=\"" + value + '\"' +
                '}';
    }
}

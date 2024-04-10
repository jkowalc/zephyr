package me.jkowalc.zephyr.domain.token;

import lombok.Getter;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public class Token {
    private final TextPosition position;
    private final TokenType type;

    public Token(TextPosition position, TokenType type) {
        this.position = position;
        this.type = type;
    }
}

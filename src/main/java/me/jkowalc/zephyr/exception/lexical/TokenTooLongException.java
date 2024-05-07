package me.jkowalc.zephyr.exception.lexical;

import me.jkowalc.zephyr.util.TextPosition;

public class TokenTooLongException extends LexicalException {
    public TokenTooLongException(String message, TextPosition position) {
        super(message, position);
    }
}

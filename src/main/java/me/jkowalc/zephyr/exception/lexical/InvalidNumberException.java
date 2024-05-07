package me.jkowalc.zephyr.exception.lexical;

import me.jkowalc.zephyr.util.TextPosition;

public class InvalidNumberException extends LexicalException {

    public InvalidNumberException(String message, TextPosition position) {
        super(message, position);
    }
}

package me.jkowalc.zephyr.exception.lexical;

import me.jkowalc.zephyr.util.TextPosition;

public class InvalidCharacterException extends LexicalException {

    public InvalidCharacterException(String message, TextPosition position) {
        super(message, position);
    }
}

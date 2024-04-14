package me.jkowalc.zephyr.exception.lexical;

import me.jkowalc.zephyr.util.TextPosition;

public class InvalidEscapeSequenceException extends LexicalException{
    public InvalidEscapeSequenceException(String message, TextPosition position) {
        super(message, position);
    }
}

package me.jkowalc.zephyr.exception.lexical;

import me.jkowalc.zephyr.util.TextPosition;

public class UnterminatedStringException extends LexicalException{
    public UnterminatedStringException(String message, TextPosition position) {
        super(message, position);
    }
}

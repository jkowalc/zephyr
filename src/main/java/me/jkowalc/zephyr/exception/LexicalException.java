package me.jkowalc.zephyr.exception;

import me.jkowalc.zephyr.util.TextPosition;

public class LexicalException extends ZephyrException {
    public LexicalException(String message, TextPosition position) {
        super(message, position);
    }
}

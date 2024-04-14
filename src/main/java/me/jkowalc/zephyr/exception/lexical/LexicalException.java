package me.jkowalc.zephyr.exception.lexical;

import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.TextPosition;

public class LexicalException extends ZephyrException {
    public LexicalException(String message, TextPosition position) {
        super(message, position);
    }
}

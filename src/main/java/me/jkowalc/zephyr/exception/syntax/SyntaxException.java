package me.jkowalc.zephyr.exception.syntax;

import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.TextPosition;

public class SyntaxException extends ZephyrException {
    public SyntaxException(String message, TextPosition position) {
        super(message, position);
    }
}

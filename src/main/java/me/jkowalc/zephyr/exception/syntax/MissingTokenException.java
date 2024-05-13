package me.jkowalc.zephyr.exception.syntax;

import me.jkowalc.zephyr.util.TextPosition;

public class MissingTokenException extends SyntaxException{
    public MissingTokenException(String message, TextPosition position) {
        super(message, position);
    }
}

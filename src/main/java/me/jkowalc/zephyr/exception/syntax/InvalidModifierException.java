package me.jkowalc.zephyr.exception.syntax;

import me.jkowalc.zephyr.util.TextPosition;

public class InvalidModifierException extends SyntaxException{
    public InvalidModifierException(String message, TextPosition position) {
        super(message, position);
    }
}

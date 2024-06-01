package me.jkowalc.zephyr.exception.syntax;

import me.jkowalc.zephyr.util.TextPosition;

public class MultipleDefinitionException extends SyntaxException {
    public MultipleDefinitionException(String message, TextPosition position) {
        super(message, position);
    }
}

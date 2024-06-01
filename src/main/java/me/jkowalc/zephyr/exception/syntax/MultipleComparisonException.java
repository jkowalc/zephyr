package me.jkowalc.zephyr.exception.syntax;

import me.jkowalc.zephyr.util.TextPosition;

public class MultipleComparisonException extends SyntaxException {
    public MultipleComparisonException(String message, TextPosition position) {
        super(message, position);
    }
}

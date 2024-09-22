package me.jkowalc.zephyr.exception.syntax;

import me.jkowalc.zephyr.util.TextPosition;

public class MissingLiteralException extends SyntaxException{
    public MissingLiteralException(TextPosition position) {
        super("Missing literal expression inside a struct field", position);
    }
}

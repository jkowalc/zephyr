package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.util.TextPosition;

public class InvalidFieldAccessException extends AnalizerException {
    public InvalidFieldAccessException(Assignable target, String field, TextPosition position) {
        super("Cannot access field " + field + " on " + target.toString(), position);
    }
}

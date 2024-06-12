package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.node.expression.Assignable;
import me.jkowalc.zephyr.util.TextPosition;

public class InvalidFieldAccessException extends AnalizerException {
    private static String generateMessage(Assignable target, String field, boolean correctType) {
        return correctType ? "Cannot access field " + field + " on " + target : "Cannot access field " + field + " on " + target.toString() + " (" + target + " is not a struct)";
    }
    public InvalidFieldAccessException(Assignable target, String field, boolean correctType, TextPosition position) {
        super(generateMessage(target, field, correctType), position);
    }
}

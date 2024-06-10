package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.util.TextPosition;

public class InvalidTypeForOperationException extends AnalizerException {
    public InvalidTypeForOperationException(StaticType left, StaticType right, String operator, TextPosition position) {
        super("Invalid type for operation " + operator + ". Passed " + left.getCategory() + " and " + right.getCategory(), position);
    }
}

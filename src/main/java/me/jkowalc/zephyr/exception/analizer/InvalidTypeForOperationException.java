package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.util.TextPosition;

public class InvalidTypeForOperationException extends AnalizerException {
    public InvalidTypeForOperationException(BareStaticType left, BareStaticType right, String operator, TextPosition position) {
        super("Invalid type for operation " + operator + ". Passed " + left.getCategory() + " and " + right.getCategory(), position);
    }
}

package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

public class InvalidTypeForOperationException extends AnalizerException {
    private static String formatTypes(List<BareStaticType> types) {
        StringBuilder builder = new StringBuilder();
        for (BareStaticType type : types) {
            builder.append(type).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
    public InvalidTypeForOperationException(List<BareStaticType> types, String operator, TextPosition position) {
        super("Invalid type for operation " + operator + ". Passed " + formatTypes(types), position);
    }
}

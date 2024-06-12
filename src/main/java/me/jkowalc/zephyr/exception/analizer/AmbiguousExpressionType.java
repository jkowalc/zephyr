package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.util.TextPosition;

import java.util.List;

public class AmbiguousExpressionType extends AnalizerException {
    private static String formatPossibleTypes(List<BareStaticType> possibleTypes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < possibleTypes.size(); i++) {
            builder.append(possibleTypes.get(i).getCategory());
            if (i != possibleTypes.size() - 1) {
                builder.append(", ");
            }
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
    public AmbiguousExpressionType(List<BareStaticType> possibleTypes, TextPosition position) {
        super("Type of expression cannot be detected statically (could be any of: " + formatPossibleTypes(possibleTypes) + ")", position);
    }
}
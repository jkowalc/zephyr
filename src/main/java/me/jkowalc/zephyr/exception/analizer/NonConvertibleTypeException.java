package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.type.StaticType;
import me.jkowalc.zephyr.util.TextPosition;

public class NonConvertibleTypeException extends AnalizerException {
    public NonConvertibleTypeException(StaticType expected, StaticType got, TextPosition position) {
        super("Wrong type", position);
    }
}

package me.jkowalc.zephyr.exception.analizer;

import me.jkowalc.zephyr.domain.type.BareStaticType;
import me.jkowalc.zephyr.util.TextPosition;

public class NonConvertibleTypeException extends AnalyzerException {
    public NonConvertibleTypeException(BareStaticType expected, BareStaticType got, TextPosition position) {
        super("Cannot convert type " + got + " to " + expected, position);
    }
}

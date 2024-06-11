package me.jkowalc.zephyr.exception.runtime;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.util.TextPosition;

public class ConversionException extends ZephyrRuntimeException{
    public ConversionException(Value value, TypeCategory target, TextPosition position) {
        super("Cannot convert " + value.toString() + " to " + target.name(), position);
    }
}

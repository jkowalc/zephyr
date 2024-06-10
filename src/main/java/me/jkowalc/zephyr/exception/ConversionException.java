package me.jkowalc.zephyr.exception;

import lombok.Getter;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

@Getter
public class ConversionException extends Exception {
    private final Value value;
    private final TypeCategory target;
    public ConversionException(Value value, TypeCategory target) {
        super("ConversionException");
        this.value = value;
        this.target = target;
    }
}

package me.jkowalc.zephyr.exception.type;

import lombok.Getter;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

@Getter
public class ConversionTypeException extends TypeException {
    private final Value value;
    private final TypeCategory target;
    public ConversionTypeException(Value value, TypeCategory target) {
        super("Cannot convert " + value.toString() + " to " + target.name());
        this.value = value;
        this.target = target;
    }
}

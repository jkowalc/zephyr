package me.jkowalc.zephyr.domain.runtime.value;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

public record IntegerValue(int value) implements Value {
    @Override
    public TypeCategory getType() {
        return TypeCategory.INT;
    }

    @Override
    public Value deepCopy() {
        return null;
    }

    @Override
    public Value getValue() {
        return this;
    }
}

package me.jkowalc.zephyr.domain.runtime.value;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

public record BooleanValue(boolean value) implements Value {
    @Override
    public TypeCategory getType() {
        return TypeCategory.BOOL;
    }

    @Override
    public Value deepCopy() {
        return this;
    }

    @Override
    public Value getValue() {
        return this;
    }
}

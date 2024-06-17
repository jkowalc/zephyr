package me.jkowalc.zephyr.domain.runtime.value;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

public record FloatValue(float value) implements Value {
    @Override
    public TypeCategory getCategory() {
        return TypeCategory.FLOAT;
    }

    @Override
    public Value deepCopy() {
        return this;
    }

    @Override
    public Value getValue() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

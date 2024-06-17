package me.jkowalc.zephyr.domain.runtime.value;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

public record StringValue(String value) implements Value {

    @Override
    public TypeCategory getCategory() {
        return TypeCategory.STRING;
    }

    @Override
    public Value deepCopy() {
        // In Java strings are immutable
        return this;
    }

    @Override
    public Value getValue() {
        return this;
    }
}

package me.jkowalc.zephyr.domain.runtime.value;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;

public class VoidValue implements Value {
    @Override
    public TypeCategory getCategory() {
        return TypeCategory.VOID;
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

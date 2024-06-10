package me.jkowalc.zephyr.domain.runtime;

import me.jkowalc.zephyr.domain.type.TypeCategory;

public interface Value {
    TypeCategory getType();
    Value deepCopy();
    Value getValue();
}

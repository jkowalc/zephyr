package me.jkowalc.zephyr.domain.runtime;

import me.jkowalc.zephyr.domain.type.TypeCategory;

public interface Value {
    TypeCategory getCategory();
    Value deepCopy();
    Value getValue();
}

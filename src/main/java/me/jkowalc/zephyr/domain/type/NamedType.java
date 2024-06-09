package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

@Getter
public class NamedType extends Type {
    private final String name;
    public NamedType(String name, boolean mutable) {
        super(TypeCategory.NAMED_TYPE, mutable);
        this.name = name;
    }
}

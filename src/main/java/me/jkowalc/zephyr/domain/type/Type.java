package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

@Getter
public class Type {
    private final TypeCategory category;
    private final boolean mutable;
    private final boolean reference;
    public Type(TypeCategory category) {
        this.category = category;
        this.mutable = false;
        this.reference = false;
    }
    public Type(TypeCategory category, boolean mutable) {
        this.category = category;
        this.mutable = mutable;
        this.reference = false;
    }
    public Type(TypeCategory category, boolean mutable, boolean reference) {
        this.category = category;
        this.mutable = mutable;
        this.reference = reference;
    }
}

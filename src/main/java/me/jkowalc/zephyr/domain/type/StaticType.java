package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

@Getter
public class StaticType {
    private final TypeCategory category;
    private final boolean mutable;
    private final boolean reference;
    public StaticType(TypeCategory category) {
        this.category = category;
        this.mutable = false;
        this.reference = false;
    }
    public StaticType(TypeCategory category, boolean mutable) {
        this.category = category;
        this.mutable = mutable;
        this.reference = false;
    }
    public StaticType(TypeCategory category, boolean mutable, boolean reference) {
        this.category = category;
        this.mutable = mutable;
        this.reference = reference;
    }
    public static StaticType fromString(String type) {
        TypeCategory typeCategory = TypeCategory.fromString(type);
        return typeCategory.equals(TypeCategory.NAMED_TYPE) ? new NamedStaticType(type, false, false) : new StaticType(typeCategory);
    }
    public static StaticType fromString(String type, boolean mutable, boolean reference) {
        TypeCategory typeCategory = TypeCategory.fromString(type);
        return typeCategory.equals(TypeCategory.NAMED_TYPE) ? new NamedStaticType(type, mutable, reference) : new StaticType(typeCategory, mutable, reference);
    }

    @Override
    public String toString() {
        return category.toString();
    }
}

package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

@Getter
public class StaticType {
    private final BareStaticType bareStaticType;
    private final boolean mutable;
    private final boolean reference;
    public StaticType(BareStaticType bareStaticType) {
        this.bareStaticType = bareStaticType;
        this.mutable = false;
        this.reference = false;
    }
    public StaticType(BareStaticType bareStaticType, boolean mutable, boolean reference) {
        this.bareStaticType = bareStaticType;
        this.mutable = mutable;
        this.reference = reference;
    }
    @Override
    public String toString() {
        return bareStaticType.toString();
    }
    public static StaticType fromCategory(TypeCategory category) {
        return new StaticType(new BareStaticType(category));
    }
}

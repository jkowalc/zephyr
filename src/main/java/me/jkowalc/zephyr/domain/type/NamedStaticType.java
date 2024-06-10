package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

@Getter
public class NamedStaticType extends StaticType {
    private final String name;
    public NamedStaticType(String name, boolean mutable, boolean reference) {
        super(TypeCategory.NAMED_TYPE, mutable, reference);
        this.name = name;
    }
}

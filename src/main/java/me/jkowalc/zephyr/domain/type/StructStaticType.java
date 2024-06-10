package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

import java.util.Map;

@Getter
public class StructStaticType extends StaticType {
    private final Map<String, StaticType> fields;

    public StructStaticType(Map<String, StaticType> fields) {
        super(TypeCategory.STRUCT, false);
        this.fields = fields;
    }
}

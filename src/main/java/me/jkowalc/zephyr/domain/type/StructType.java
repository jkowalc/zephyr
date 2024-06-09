package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

import java.util.Map;

@Getter
public class StructType extends Type {
    private final Map<String, Type> fields;

    public StructType(Map<String, Type> fields) {
        super(TypeCategory.STRUCT, false);
        this.fields = fields;
    }
}

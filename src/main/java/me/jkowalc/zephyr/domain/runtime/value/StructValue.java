package me.jkowalc.zephyr.domain.runtime.value;

import lombok.Getter;
import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.util.SimpleMap;

import java.util.Map;

@Getter
public class StructValue implements Value {
    private final Map<String, Value> fields;
    @Override
    public TypeCategory getType() {
        return TypeCategory.STRUCT;
    }

    @Override
    public Value deepCopy() {
        Map<String, Value> newFields = new SimpleMap<>();
        fields.forEach((key, value) -> newFields.put(key, value.deepCopy()));
        return new StructValue(newFields);
    }

    @Override
    public Value getValue() {
        return this;
    }

    public StructValue(Map<String, Value> fields) {
        this.fields = fields;
    }
    public Value getField(String fieldName) {
        return fields.get(fieldName);
    }
    public void setField(String fieldName, Value value) {
        fields.put(fieldName, value);
    }
}

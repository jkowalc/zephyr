package me.jkowalc.zephyr.domain.runtime.value;

import me.jkowalc.zephyr.domain.runtime.Value;
import me.jkowalc.zephyr.domain.type.TypeCategory;
import me.jkowalc.zephyr.util.SimpleMap;

import java.util.Map;

public record StructValue(Map<String, Value> fields) implements Value {
    @Override
    public TypeCategory getCategory() {
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
    public Value getField(String fieldName) {
        return fields.get(fieldName);
    }
    public void setField(String fieldName, Value value) {
        this.fields.put(fieldName, value);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        fields.forEach((key, value) -> sb.append(key).append(": ").append(value).append(", "));
        if(!fields.isEmpty()) sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }
}

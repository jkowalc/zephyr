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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        fields.forEach((key, value) -> sb.append(key).append(": ").append(value).append(", "));
        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }
}

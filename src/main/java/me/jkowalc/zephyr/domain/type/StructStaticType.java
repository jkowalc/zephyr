package me.jkowalc.zephyr.domain.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = false)
public class StructStaticType extends BareStaticType {
    private final Map<String, BareStaticType> fields;

    public StructStaticType(Map<String, BareStaticType> fields) {
        super(TypeCategory.STRUCT);
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

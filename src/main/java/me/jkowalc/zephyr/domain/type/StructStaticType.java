package me.jkowalc.zephyr.domain.type;

import lombok.Getter;

import java.util.Map;

@Getter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StructStaticType that)) return false;
        if (that.getFields().size() != fields.size()) return false;
        for (Map.Entry<String, BareStaticType> entry : fields.entrySet()) {
            if (!that.fields.containsKey(entry.getKey()) || !that.fields.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}

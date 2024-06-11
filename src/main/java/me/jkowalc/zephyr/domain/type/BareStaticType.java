package me.jkowalc.zephyr.domain.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class BareStaticType {
    private final TypeCategory category;

    public BareStaticType(TypeCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return category.toString();
    }
}

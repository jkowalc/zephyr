package me.jkowalc.zephyr.domain.runtime;

import lombok.Getter;
import lombok.Setter;
import me.jkowalc.zephyr.domain.type.TypeCategory;

@Setter
@Getter
public class Reference implements Value {
    private Value value;

    public Reference(Value value) {
        this.value = value;
    }

    @Override
    public TypeCategory getCategory() {
        return value.getCategory();
    }

    @Override
    public Value deepCopy() {
        return new Reference(value.deepCopy());
    }

}

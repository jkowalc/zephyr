package me.jkowalc.zephyr.domain.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public class UnionStaticType extends BareStaticType {
    private final List<BareStaticType> alternatives;

    public UnionStaticType(List<BareStaticType> alternatives) {
        super(TypeCategory.UNION);
        this.alternatives = alternatives;
    }
}

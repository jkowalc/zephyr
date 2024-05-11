package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class FloatLiteral extends Node implements Literal {
    private final float value;

    public FloatLiteral(float value) {
        super(null, null);
        this.value = value;
    }

    public FloatLiteral(TextPosition startPosition, TextPosition endPosition, float value) {
        super(startPosition, endPosition);
        this.value = value;
    }
}
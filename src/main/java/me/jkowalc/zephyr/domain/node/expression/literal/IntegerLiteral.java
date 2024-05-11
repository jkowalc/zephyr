package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
public final class IntegerLiteral extends Node implements Literal {
    private final int value;

    public IntegerLiteral(int value) {
        super(null, null);
        this.value = value;
    }

    public IntegerLiteral(TextPosition startPosition, TextPosition endPosition, int value) {
        super(startPosition, endPosition);
        this.value = value;
    }
}

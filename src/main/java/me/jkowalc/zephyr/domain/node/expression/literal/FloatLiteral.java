package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
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

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}
package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.EqualsAndHashCode;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@EqualsAndHashCode(callSuper = false)
public final class BooleanLiteral extends Node implements Literal {
    private final boolean value;
    public BooleanLiteral(boolean value) {
        super(null);
        this.value = value;
    }
    public BooleanLiteral(TextPosition startPosition, boolean value) {
        super(startPosition);
        this.value = value;
    }
    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) throws ZephyrException {
        visitor.visit(this);
    }
}

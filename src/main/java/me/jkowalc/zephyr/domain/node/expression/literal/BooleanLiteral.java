package me.jkowalc.zephyr.domain.node.expression.literal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.util.ASTVisitor;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class BooleanLiteral extends Node implements Literal {
    private final boolean value;
    public BooleanLiteral(boolean value) {
        super(null, null);
        this.value = value;
    }
    public BooleanLiteral(TextPosition startPosition, TextPosition endPosition, boolean value) {
        super(startPosition, endPosition);
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

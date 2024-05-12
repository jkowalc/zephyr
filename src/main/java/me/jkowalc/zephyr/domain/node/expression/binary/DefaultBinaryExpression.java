package me.jkowalc.zephyr.domain.node.expression.binary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;
import me.jkowalc.zephyr.util.TextPosition;

@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class DefaultBinaryExpression extends Node implements Expression {
    private final Expression left;
    private final Expression right;

    public DefaultBinaryExpression(Expression left, Expression right) {
        super(null, null);
        this.left = left;
        this.right = right;
    }

    @Override
    protected TextPosition getDefaultStartPosition() {
        return left.getStartPosition();
    }

    @Override
    protected TextPosition getDefaultEndPosition() {
        return right.getEndPosition();
    }
}

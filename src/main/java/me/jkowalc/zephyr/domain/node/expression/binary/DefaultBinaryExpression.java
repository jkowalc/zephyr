package me.jkowalc.zephyr.domain.node.expression.binary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.jkowalc.zephyr.domain.node.Node;
import me.jkowalc.zephyr.domain.node.expression.Expression;

@Getter
@EqualsAndHashCode(callSuper = false)
public abstract class DefaultBinaryExpression extends Node implements Expression {
    private final Expression left;
    private final Expression right;

    public DefaultBinaryExpression(Expression left, Expression right) {
        super(left.getStartPosition(), right.getEndPosition());
        this.left = left;
        this.right = right;
    }
}

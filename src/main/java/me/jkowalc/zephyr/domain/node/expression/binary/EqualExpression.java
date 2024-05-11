package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class EqualExpression extends DefaultBinaryExpression {
    public EqualExpression(Expression left, Expression right) {
        super(left, right);
    }
}

package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class GreaterEqualExpression extends DefaultBinaryExpression {

    public GreaterEqualExpression(Expression left, Expression right) {
        super(left, right);
    }
}

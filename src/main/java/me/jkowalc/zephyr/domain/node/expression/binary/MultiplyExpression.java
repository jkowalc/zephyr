package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class MultiplyExpression extends DefaultBinaryExpression {

    public MultiplyExpression(Expression left, Expression right) {
        super(left, right);
    }
}

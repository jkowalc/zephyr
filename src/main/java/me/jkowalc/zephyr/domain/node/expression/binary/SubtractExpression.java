package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class SubtractExpression extends DefaultBinaryExpression {

    public SubtractExpression(Expression left, Expression right) {
        super(left, right);
    }
}

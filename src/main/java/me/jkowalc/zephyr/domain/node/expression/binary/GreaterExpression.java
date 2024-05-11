package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class GreaterExpression extends DefaultBinaryExpression {

    public GreaterExpression(Expression left, Expression right) {
        super(left, right);
    }
}

package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class DivideExpression extends DefaultBinaryExpression {
    public DivideExpression(Expression left, Expression right) {
        super(left, right);
    }
}

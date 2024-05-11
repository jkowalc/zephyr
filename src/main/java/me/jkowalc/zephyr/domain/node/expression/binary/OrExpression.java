package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class OrExpression extends DefaultBinaryExpression {

    public OrExpression(Expression left, Expression right) {
        super(left, right);
    }
}
package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;


public final class AddExpression extends DefaultBinaryExpression {

    public AddExpression(Expression left, Expression right) {
        super(left, right);
    }
}

package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class AndExpression extends DefaultBinaryExpression {

    public AndExpression(Expression left, Expression right) {
        super(left, right);
    }
}

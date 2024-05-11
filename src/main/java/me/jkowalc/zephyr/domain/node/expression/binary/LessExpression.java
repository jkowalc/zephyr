package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class LessExpression extends DefaultBinaryExpression {

    public LessExpression(Expression left, Expression right) {
        super(left, right);
    }
}

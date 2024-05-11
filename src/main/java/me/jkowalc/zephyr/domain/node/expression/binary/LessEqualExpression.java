package me.jkowalc.zephyr.domain.node.expression.binary;

import me.jkowalc.zephyr.domain.node.expression.Expression;

public final class LessEqualExpression extends DefaultBinaryExpression{

    public LessEqualExpression(Expression left, Expression right) {
        super(left, right);
    }
}
